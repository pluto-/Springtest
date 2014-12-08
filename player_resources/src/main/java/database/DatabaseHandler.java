package database;

import java.sql.SQLException;
import java.util.List;

import records.playerresources.Building;
import records.playerresources.Resource;

/**
 * Created by Jonas on 2014-12-05.
 */
public class DatabaseHandler {

    public static List<Resource> getPlayerResources(int playerId) throws SQLException {
        List<Resource> resources = Resource.selectAll(Resource.class, "SELECT * FROM resources WHERE player_id = #1#", playerId);
        return resources;
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
