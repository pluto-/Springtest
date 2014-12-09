import com.distributed.springtest.utils.records.gamecontent.BuildingCost;
import com.distributed.springtest.utils.records.gamecontent.BuildingInfo;
import com.distributed.springtest.utils.records.playerresources.Construction;
import database.DatabaseHandler;
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
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Jonas on 2014-12-05.
 */
@RestController
public class PlayerResourcesController {

    @RequestMapping("/player_resources")
    public Object getPlayerResources(@RequestParam(value="player_id") int playerId) {
        List<Resource> resources = null;
        try {

            updatePlayerResources(playerId);
            resources = DatabaseHandler.getPlayerResources(playerId);

        }  catch (SQLException | IOException e) {
            e.printStackTrace();
            return new ResponseEntity<Object>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return resources;
    }

    @RequestMapping("/player_buildings")
    public Object getPlayerBuildings(@RequestParam(value="player_id") int playerId) {

        List<Building> buildings = null;
        try {
            buildings = DatabaseHandler.getPlayerBuildings(playerId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<Object>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return buildings;
    }

    @RequestMapping(value="/player_buy_building",  method= RequestMethod.POST)
    public Object buyBuilding(@RequestParam(value="player_id") int playerId, @RequestParam(value="building_id") int buildingId) {
        List<BuildingCost> costs = null;
        List<Resource> playerResources = null;
        try {
            costs = getBuildingCost(buildingId);
            playerResources = DatabaseHandler.getPlayerResources(playerId);
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            return new ResponseEntity<Object>("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if(hasEnoughMaterials(playerResources, costs)) {

            Construction construction = new Construction();
            try {
                // Removing costs from playerResources list.
                for (BuildingCost cost : costs) {
                    for (Resource resource : playerResources) {
                        if (resource.getResourceId().equals(cost.getResourceId())) {
                            resource.setAmount(resource.getAmount() - cost.getAmount());
                            resource.save();
                        }
                    }
                }

                construction.setPlayerId(playerId);
                construction.setBuildingId(buildingId);
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
        return new ResponseEntity<Object>("Not enough resources.", HttpStatus.METHOD_NOT_ALLOWED);
    }

    private boolean hasEnoughMaterials(List<Resource> playerResources, List<BuildingCost> costs) {

        boolean isEnough;
        for(BuildingCost cost : costs) {
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

    private void updatePlayerResources(int playerId) throws SQLException, IOException {
        List<Resource> resources = DatabaseHandler.getPlayerResources(playerId);
        List<Building> buildings = DatabaseHandler.getPlayerBuildings(playerId);
        List<BuildingInfo> buildingsInfo = getBuildingsInfo();
        long currentTime = System.currentTimeMillis();
        for(Building building : buildings) {
            long lastUpdated = building.getLastUpdated().getTime();
            long differenceMilli = currentTime - lastUpdated;
            long differenceSec = Math.round(differenceMilli / 1000);
            for(BuildingInfo buildingInfo : buildingsInfo) {
                if(buildingInfo.getId().equals(building.getBuildingId())) {
                    int amountPerSecond = buildingInfo.getGeneratedAmount();
                    int nrOfBuildings = building.getAmount();
                    for(Resource resource : resources) {
                        if(resource.getResourceId().equals(buildingInfo.getGeneratedId())) {

                            building.setLastUpdated(new Timestamp(currentTime));
                            resource.setAmount(resource.getAmount() + (int) (nrOfBuildings * amountPerSecond * differenceSec));
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
                    if((int)((currentTime - construction.getStartedAt().getTime()) / 1000) >= buildingInfo.getBuildtime()) {

                        // Increase the resource.
                        for(Resource resource : resources) {
                            if(resource.getResourceId().equals(buildingInfo.getGeneratedId())) {
                                long timeFinishedConstruction = construction.getStartedAt().getTime() + buildingInfo.getBuildtime();
                                int generatedAmount = (int)((currentTime - timeFinishedConstruction)/1000) * buildingInfo.getGeneratedAmount();
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

        DatabaseHandler.setPlayerResources(playerId, resources);
        DatabaseHandler.setPlayerBuildings(playerId, buildings);
    }

    private List<BuildingInfo> getBuildingsInfo() throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String uri = PropertiesLoader.getAddressAndPort() + "/getBuildings";
        BuildingInfo[] buildingInfos = restTemplate.getForObject(uri, BuildingInfo[].class);
        return Arrays.asList(buildingInfos);
    }

    private List<BuildingCost> getBuildingCost(int buildingId) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String uri = PropertiesLoader.getAddressAndPort() + "/getBuildingCost/" + buildingId;
        BuildingCost[] buildingCosts = restTemplate.getForObject(uri, BuildingCost[].class);
        return Arrays.asList(buildingCosts);
    }
}
