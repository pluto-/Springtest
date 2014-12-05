import database.DatabaseHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import player.PlayerResource;

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
    public List<PlayerResource> getPlayerResources(@RequestParam(value="player_id") int playerId) {
        updatePlayerResources(playerId);

        return null;
    }

    private void updatePlayerResources(int playerId) throws SQLException {
        List<PlayerResource> resources = DatabaseHandler.getPlayerResources(playerId);
        List<>
        long currentTime = System.currentTimeMillis();
        for(PlayerResource resource : resources) {
            long lastUpdated = resource.getLastUpdated().getTime();
            long differenceMilli = currentTime - lastUpdated;
            long differenceSec = Math.round(differenceMilli / 1000);

        }
    }
}
