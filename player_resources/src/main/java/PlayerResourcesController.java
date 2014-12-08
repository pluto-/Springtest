import database.DatabaseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import records.playerresources.Building;
import records.playerresources.Resource;
import records.playerresources.BuildingInfo;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by Jonas on 2014-12-05.
 */
@RestController
public class PlayerResourcesController {

    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/player_resources")
    public Object getPlayerResources(@RequestParam(value="player_id") int playerId) {
        try {
            updatePlayerResources(playerId);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Resource> resources = null;
        try {
            resources = DatabaseHandler.getPlayerResources(playerId);
        } catch (SQLException e) {
            e.printStackTrace();
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
        }
        return buildings;
    }


    @RequestMapping(value="/player_add_building",  method= RequestMethod.POST)
    public Object addBuilding(@RequestParam(value="player_id") int playerId, @RequestParam(value="building_id") int buildingId) {

        List<Building> buildings = null;
        try {
            buildings = DatabaseHandler.getPlayerBuildings(playerId);
        } catch (SQLException e) {
            e.printStackTrace();
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return buildings;
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
                if(buildingInfo.getId() == building.getBuildingId()) {
                    int amountPerSecond = buildingInfo.getProduces_resource_amount();
                    int nrOfBuildings = building.getAmount();
                    for(Resource resource : resources) {
                        if(resource.getResourceId() == buildingInfo.getProduces_resource_id()) {

                            building.setLastUpdated(new Timestamp(currentTime));
                            resource.setAmount(resource.getAmount() + (int) (nrOfBuildings * amountPerSecond * differenceSec));
                        }

                    }
                }
            }
        }
        DatabaseHandler.setPlayerResources(playerId, resources);
        DatabaseHandler.setPlayerBuildings(playerId, buildings);
    }

    private List<BuildingInfo> getBuildingsInfo() throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        String uri = PropertiesLoader.getAddressAndPort() + "/getBuildings";
        List<BuildingInfo> buildings = restTemplate.getForObject(uri, List.class);
        return buildings;

        /*
        List<BuildingInfo> buildingInfo = new ArrayList<BuildingInfo>();
        buildingInfo.add(new BuildingInfo(1, "Lumber Mill", 1, 5));
        return buildingInfo;*/
    }
}
