package database;

import java.sql.SQLException;
import java.util.List;

import player.PlayerResource;

/**
 * Created by Jonas on 2014-12-05.
 */
public class DatabaseHandler {

    public static List<PlayerResource> getPlayerResources(int playerId) throws SQLException {
        return PlayerResource.findAll(PlayerResource.class, "player_id", playerId);
    }
}
