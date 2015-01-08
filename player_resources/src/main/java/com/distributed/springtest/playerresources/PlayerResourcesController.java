package com.distributed.springtest.playerresources;

import com.distributed.springtest.utils.records.gamecontent.BuildingCostInfo;
import com.distributed.springtest.utils.security.DigestHandler;
import com.distributed.springtest.utils.security.DigestRestTemplate;
import com.distributed.springtest.utils.wrappers.BuyBuildingWrapper;
import com.distributed.springtest.utils.wrappers.PlayerResourceModificationWrapper;
import com.distributed.springtest.utils.wrappers.PlayerStateWrapper;
import com.distributed.springtest.utils.records.gamecontent.BuildingInfo;
import com.distributed.springtest.utils.records.playerresources.Construction;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.distributed.springtest.utils.records.playerresources.Building;
import com.distributed.springtest.utils.records.playerresources.Resource;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Jonas on 2014-12-05.
 */
@RestController
public class PlayerResourcesController implements InitializingBean {

    static private String gameContentURL;
    static protected DigestHandler digestHandler;
    static private String username;
    static private String hashedPassword;

    static private DigestRestTemplate gameContentRestTemplate;

    @Value("${hosts.gamecontent}")
    public void setGameContentURLName(String gameContentURL) {
        PlayerResourcesController.gameContentURL = gameContentURL;
    }

    @Value("${digesthandler.path}")
    public void setDigestHandler(String filePath) {
        try {
            PlayerResourcesController.digestHandler = new DigestHandler(new FileInputStream(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Value("${subsystem.username}")
    public void setUsername(String username) {
        PlayerResourcesController.username = username;
    }

    @Value("${subsystem.password}")
    public void setHashedPassword(String password) {
        PlayerResourcesController.hashedPassword = password;
    }

    /**
     * Modifies a resource for the specified player.
     * @param wrapper wrapper containing information about the resource and amount to be changed.
     * @return
     */
    @RequestMapping(value="/resources/modify", method=RequestMethod.PUT)
    public Object modifyPlayerResource(@RequestBody PlayerResourceModificationWrapper wrapper) {

        try {

            updatePlayerResources(wrapper.getPlayerId());

            Resource resource = Resource.select(Resource.class, "SELECT * FROM resources WHERE player_id = #1# AND resource_id = #2#", wrapper.getPlayerId(), wrapper.getResourceId());

            if(resource == null) {
                resource = new Resource();
                resource.setPlayerId(wrapper.getPlayerId());
                resource.setResourceId(wrapper.getResourceId());
                resource.setAmount(0.0);
            }

            if((resource.getAmount() + wrapper.getResourceAmount()) < 0) {
                return new ResponseEntity<Object>("Not enough of that resource.", HttpStatus.CONFLICT);
            }

            resource.setAmount(resource.getAmount() + wrapper.getResourceAmount());
            resource.save();
            resource.transaction().commit();


            return new ResponseEntity<Object>(HttpStatus.OK);

        } catch (SQLException |IOException e) {
            e.printStackTrace();
            return new ResponseEntity<Object>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Returns the resources of the specified player.
     *
     * @param playerId ID of player.
     * @return
     */
    @RequestMapping("/{id}/resources")
    public Object getPlayerResources(@PathVariable("id") Integer playerId) {
        try {

            updatePlayerResources(playerId);

            List<Resource> resources = Resource.selectAll(Resource.class, "SELECT * FROM resources WHERE player_id = #1#", playerId);

            return resources;

        } catch (SQLException |IOException e) {
            e.printStackTrace();
            return new ResponseEntity<Object>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Returns the digest counter of the user specified in the "username"-header.
     * @param request the request containing the "username"-header.
     * @return
     */
    @RequestMapping("/counter")
    public Object getPlayerResources(HttpServletRequest request) {
        int counter = digestHandler.getCounter(request.getHeader("username"));
        if(counter == -1) {
            return new ResponseEntity<Object>("Username does not exist.", HttpStatus.UNAUTHORIZED);
        }
        return counter;
    }

    /**
     * Returns information about the specified resource of the specified player.
     *
     * @param playerId ID of player.
     * @param resourceId ID of resource.
     * @return Information about the resource (amount).
     */
    @RequestMapping("/{player_id}/resources/{resource_id}")
    public Object getPlayerResource(@PathVariable("player_id") Integer playerId, @PathVariable("resource_id") Integer resourceId) {

        try {

            updatePlayerResources(playerId);

            Resource resource = Resource.select(Resource.class, "SELECT * FROM resources WHERE player_id = #1# AND resource_id = #2#", playerId, resourceId);

            return resource;

        } catch (SQLException |IOException e) {
            e.printStackTrace();
            return new ResponseEntity<Object>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Constructs a PlayerStateWrapper containing information about the players resources, buildings and constructions.
     *
     * @param playerId ID of player.
     * @return the wrapper.
     */
    @RequestMapping("/state/{id}")
    public Object getState(@PathVariable("id") Integer playerId) {

        try {

            updatePlayerResources(playerId);

            List<Resource> resources = Resource.selectAll(Resource.class, "SELECT * FROM resources WHERE player_id = #1#", playerId);
            List<Building> buildings = Building.selectAll(Building.class, "SELECT * FROM buildings WHERE player_id = #1#", playerId);
            List<Construction> constructions = Construction.selectAll(Construction.class, "SELECT * FROM construction WHERE player_id = #1#", playerId);

            PlayerStateWrapper wrapper = new PlayerStateWrapper(resources, buildings, constructions);

            return wrapper;

        } catch (SQLException |IOException e) {
            e.printStackTrace();
            return new ResponseEntity<Object>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Attempts to buy a building. If the resources aren't enough a CONFLICT response will be returned.
     *
     * @param wrapper contains information about what building the user wants to buy.
     * @return OK if success, otherwise an error status.
     */
    @RequestMapping(value="/building/buy",  method= RequestMethod.POST)
    public Object buyBuilding(@RequestBody BuyBuildingWrapper wrapper) {
        List<BuildingCostInfo> costs = null;
        List<Resource> playerResources = null;
        try {
            costs = getBuildingCost(wrapper.getBuildingId());
            playerResources = Resource.selectAll(Resource.class, "SELECT * FROM resources WHERE player_id = #1#", wrapper.getPlayerId());
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<Object>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(hasEnoughMaterials(playerResources, costs)) {

            Construction construction = new Construction();
            try {
                // Removing costs from playerResources list.
                for (BuildingCostInfo cost : costs) {
                    for (Resource resource : playerResources) {
                        if (resource.getResourceId().equals(cost.getResourceId())) {
                            resource.setAmount(resource.getAmount() - cost.getAmount());
                            resource.save();
                        }
                    }
                }

                construction.setPlayerId(wrapper.getPlayerId());
                construction.setBuildingId(wrapper.getBuildingId());
                construction.setStartedAt(new Timestamp(System.currentTimeMillis()));
                construction.save();
            } catch(SQLException e) {
                e.printStackTrace();
                return new ResponseEntity<Object>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
            try {
                construction.transaction().commit();
            } catch (SQLException e) {
                construction.transaction().close();
                e.printStackTrace();
                return new ResponseEntity<Object>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
            }

            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<Object>("Not enough resources.", HttpStatus.CONFLICT);
    }

    /**
     * Checks whether the player has enough resources.
     * @param playerResources the resources of the player.
     * @param costs the costs.
     * @return true if enough resources, otherwise false.
     */
    static private boolean hasEnoughMaterials(List<Resource> playerResources, List<BuildingCostInfo> costs) {

        boolean isEnough;
        for(BuildingCostInfo cost : costs) {
            isEnough = false;

            for(Resource resource : playerResources) {
                if(resource.getResourceId().equals(cost.getResourceId())) {
                    if(resource.getAmount() >= cost.getAmount()) {
                        isEnough = true;
                        break;
                    }
                }
            }

            if(!isEnough) {
                return false;
            }
        }

        return true;
    }

    /**
     * Updates the resources of a player.
     *
     * @param playerId ID of the player.
     * @throws SQLException
     * @throws IOException
     */
    static protected void updatePlayerResources(int playerId) throws SQLException, IOException {
        List<Resource> resources = Resource.selectAll(Resource.class, "SELECT * FROM resources WHERE player_id = #1#", playerId);

        Map<Integer, Resource> resourceMap = new HashMap<>();
        for(Resource resource : resources) {
            resourceMap.put(resource.getResourceId(), resource);
        }
        List<Building> buildings = Building.selectAll(Building.class, "SELECT * FROM buildings WHERE player_id = #1#", playerId);

        Map<Integer, Building> buildingMap = new HashMap<>();
        for(Building building : buildings) {
            buildingMap.put(building.getBuildingId(), building);
        }

        Map<Integer, BuildingInfo> buildingInfoMap = getBuildingsInfo();
        long currentTime = System.currentTimeMillis();
        BuildingInfo buildingInfo;

        // Increase resources.
        for(Building building : buildings) {
            long lastUpdated = building.getLastUpdated().getTime();
            long differenceMilli = currentTime - lastUpdated;
            long differenceSec = Math.round(differenceMilli / 1000);

            buildingInfo = buildingInfoMap.get(building.getBuildingId());

            float amountPerSecond = buildingInfo.getGeneratedAmount();
            int nrOfBuildings = building.getAmount();

            Resource resource = resourceMap.get(buildingInfo.getGeneratedId());
            if(resource == null) {
                resource = new Resource();
                resource.setPlayerId(playerId);
                resource.setResourceId(buildingInfo.getGeneratedId());
                resource.setAmount(0.0);
                resourceMap.put(resource.getResourceId(), resource);
            }
            resource.setAmount(resource.getAmount() + (nrOfBuildings * amountPerSecond * differenceSec));
            resource.save();

            building.setLastUpdated(new Timestamp(currentTime));
            building.save();

        }



        // Check finished constructions.
        List<Construction> constructions = Construction.selectAll(Construction.class, "SELECT * FROM construction WHERE player_id = #1#", playerId);
        for(Construction construction : constructions) {
            buildingInfo = buildingInfoMap.get(construction.getBuildingId());
            if(((currentTime - construction.getStartedAt().getTime()) / 1000) >= buildingInfo.getBuildtime()) {
                // Increase the resource.
                Resource resource = resourceMap.get(buildingInfo.getGeneratedId());
                if(resource == null) {
                    resource = new Resource();
                    resource.setPlayerId(playerId);
                    resource.setResourceId(buildingInfo.getGeneratedId());
                    resource.setAmount(0.0);
                    resourceMap.put(resource.getResourceId(), resource);
                }
                long timeFinishedConstruction = construction.getStartedAt().getTime() + buildingInfo.getBuildtime();
                double generatedAmount = ((currentTime - timeFinishedConstruction)/1000) * buildingInfo.getGeneratedAmount();
                resource.setAmount(resource.getAmount() + generatedAmount);
                resource.save();

                // Move to Buildings.
                Building building = buildingMap.get(construction.getBuildingId());
                if(building == null) {
                    building = new Building();
                    building.setBuildingId(buildingInfo.getId());
                    building.setPlayerId(playerId);
                    building.setAmount(0);
                    building.setLastUpdated(new Timestamp(currentTime));
                    buildingMap.put(building.getBuildingId(), building);
                }
                building.setAmount(building.getAmount() + 1);
                building.save();

                Construction.deleteById(Construction.class, construction.getId());
            }

        }

        new Resource().transaction().commit();
    }

    static private Map<Integer, BuildingInfo> getBuildingsInfo() throws IOException {
        String uri = gameContentURL + "/buildings";
        ResponseEntity<BuildingInfo[]> buildingInfos = gameContentRestTemplate.get(uri, BuildingInfo[].class);

        Map<Integer, BuildingInfo> buildingInfoMap = new HashMap<>();
        for(BuildingInfo buildingInfo : buildingInfos.getBody()) {
            buildingInfoMap.put(buildingInfo.getId(), buildingInfo);
        }
        return buildingInfoMap;
    }

    static private List<BuildingCostInfo> getBuildingCost(int buildingId) throws IOException {
        String uri = gameContentURL + "/buildings/"+ buildingId+"/costs";

        ResponseEntity<BuildingCostInfo[]> buildingCosts = gameContentRestTemplate.get(uri, BuildingCostInfo[].class);
        return Arrays.asList(buildingCosts.getBody());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        gameContentRestTemplate = new DigestRestTemplate(gameContentURL, username, hashedPassword);
    }
}
