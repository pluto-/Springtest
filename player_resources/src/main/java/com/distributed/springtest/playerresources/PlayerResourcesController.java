package com.distributed.springtest.playerresources;

import com.distributed.springtest.utils.exceptions.NotEnoughResourcesException;
import com.distributed.springtest.utils.records.gamecontent.BuildingCostInfo;
import com.distributed.springtest.utils.wrappers.BuyBuildingWrapper;
import com.distributed.springtest.utils.wrappers.PlayerStateWrapper;
import com.distributed.springtest.utils.records.gamecontent.BuildingInfo;
import com.distributed.springtest.utils.records.playerresources.Construction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import com.distributed.springtest.utils.records.playerresources.Building;
import com.distributed.springtest.utils.records.playerresources.Resource;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Jonas on 2014-12-05.
 */
@RestController
public class PlayerResourcesController {

    @RequestMapping("/state/{id}")
    public Object getState(@PathVariable("id") Integer playerId) {

        try {

            updatePlayerResources(playerId);

            List<Resource> resources = Resource.selectAll(Resource.class, "SELECT * FROM resources WHERE player_id = #1#", playerId);
            List<Building> buildings = Building.selectAll(Building.class, "SELECT * FROM buildings WHERE player_id = #1#", playerId);
            List<Construction> constructions = Construction.selectAll(Construction.class, "SELECT * FROM construction WHERE player_id = #1#", playerId);

            PlayerStateWrapper wrapper = new PlayerStateWrapper(resources, buildings, constructions);



            System.err.println("RETURNING!");
            return wrapper;

        } catch (SQLException |IOException e) {
            e.printStackTrace();
            return new ResponseEntity<Object>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value="/building/buy",  method= RequestMethod.POST)
    public Object buyBuilding(@RequestBody BuyBuildingWrapper wrapper) throws NotEnoughResourcesException {
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
        throw new NotEnoughResourcesException();
    }

    private boolean hasEnoughMaterials(List<Resource> playerResources, List<BuildingCostInfo> costs) {

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

    protected static void updatePlayerResources(int playerId) throws SQLException, IOException {
        List<Resource> resources = Resource.selectAll(Resource.class, "SELECT * FROM resources WHERE player_id = #1#", playerId);
        List<Building> buildings = Building.selectAll(Building.class, "SELECT * FROM buildings WHERE player_id = #1#", playerId);
        List<BuildingInfo> buildingsInfo = getBuildingsInfo();
        long currentTime = System.currentTimeMillis();
        for(Building building : buildings) {
            long lastUpdated = building.getLastUpdated().getTime();
            long differenceMilli = currentTime - lastUpdated;
            long differenceSec = Math.round(differenceMilli / 1000);
            for(BuildingInfo buildingInfo : buildingsInfo) {
                if(buildingInfo.getId().equals(building.getBuildingId())) {
                    float amountPerSecond = buildingInfo.getGeneratedAmount();
                    int nrOfBuildings = building.getAmount();
                    for(Resource resource : resources) {
                        if(resource.getResourceId().equals(buildingInfo.getGeneratedId())) {

                            building.setLastUpdated(new Timestamp(currentTime));
                            resource.setAmount(resource.getAmount() + (nrOfBuildings * amountPerSecond * differenceSec));
                        }

                    }
                }
            }
        }

        // Check finished constructions.
        List<Construction> constructions = Construction.selectAll(Construction.class, "SELECT * FROM construction WHERE player_id = #1#", playerId);
        for(Construction construction : constructions) {
            for(BuildingInfo buildingInfo : buildingsInfo) {
                if(buildingInfo.getId().equals(construction.getBuildingId())) {
                    if(((currentTime - construction.getStartedAt().getTime()) / 1000) >= buildingInfo.getBuildtime()) {
                        // Increase the resource.
                        for(Resource resource : resources) {
                            if(resource.getResourceId().equals(buildingInfo.getGeneratedId())) {
                                long timeFinishedConstruction = construction.getStartedAt().getTime() + buildingInfo.getBuildtime();
                                double generatedAmount = ((currentTime - timeFinishedConstruction)/1000) * buildingInfo.getGeneratedAmount();
                                resource.setAmount(resource.getAmount() + generatedAmount);
                            }
                            break;
                        }

                        // Move to Buildings.
                        for(Building building : buildings) {
                            if(building.getBuildingId().equals(construction.getBuildingId())) {
                                building.setAmount(building.getAmount() + 1);
                                break;
                            }
                        }
                        Construction.deleteById(Construction.class, construction.getId());
                    }
                    break;
                }
            }
        }

        // Store new data in database.
        for(Resource resource : resources) {
            resource.save();
        }
        for(Building building : buildings) {
            building.save();
        }
        new Resource().transaction().commit();
    }

    private static List<BuildingInfo> getBuildingsInfo() throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String uri = PropertiesLoader.getAddressAndPort() + "/buildings";
        BuildingInfo[] buildingInfos = restTemplate.getForObject(uri, BuildingInfo[].class);
        return Arrays.asList(buildingInfos);
    }

    private static List<BuildingCostInfo> getBuildingCost(int buildingId) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String uri = PropertiesLoader.getAddressAndPort() + "/buildings/"+ buildingId+"/costs";
        System.err.println(uri);
        BuildingCostInfo[] buildingCosts = restTemplate.getForObject(uri, BuildingCostInfo[].class);
        return Arrays.asList(buildingCosts);
    }
}
