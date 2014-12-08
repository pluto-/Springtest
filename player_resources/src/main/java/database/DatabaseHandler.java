package database;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.distributed.springtest.utils.records.playerresources.Building;
import com.distributed.springtest.utils.records.playerresources.Resource;

/**
 * Created by Jonas on 2014-12-05.
 */
public class DatabaseHandler {

    public static List<Resource> getPlayerResources(int playerId) throws SQLException {
        List<Resource> resources = Resource.selectAll(Resource.class, "SELECT * FROM resources WHERE player_id = #1#", playerId);
        return resources;
    }
    public static void addBuilding(int playerId, int buildingId) throws SQLException {
        Building building = Building.select(Building.class, "SELECT * FROM buildings WHERE player_id = #1# AND building_id = #2#", playerId, buildingId);
        if(building == null) {
            building = new Building();
            building.setAmount(1);
            building.setBuildingId(buildingId);
            building.setPlayerId(playerId);
            building.setLastUpdated(new Timestamp(System.currentTimeMillis()));
            building.save();
            building.transaction().commit();
        } else {
            building.setAmount(building.getAmount() + 1);
            building.save();
            building.transaction().commit();
        }
    }

    public static List<Building> getPlayerBuildings(int playerId) throws SQLException {
        return Building.selectAll(Building.class, "SELECT * FROM buildings WHERE player_id = #1#", playerId);
    }
    public static void setPlayerResources(int playerId, List<Resource> resources) throws SQLException {
        for(Resource resource : resources) {
            resource.save();
            resource.transaction().commit();
        }
    }
    public static void setPlayerBuildings(int playerId, List<Building> buildings) throws SQLException {
        for(Building building : buildings) {
            building.save();
            building.transaction().commit();
        }
    }
}
