package player;

import com.jajja.jorm.Jorm;
import com.jajja.jorm.Record;
import java.sql.Timestamp;

@Jorm(database="player_resources", schema="public", table="player_resources", primaryKey="player_id")
public class PlayerResource extends Record {
    public Integer getPlayerId() {
        return get("player_id", Integer.class);
    }

    public void setPlayerId(Integer playerId) {
        set("player_id", playerId);
    }

    public Integer getResourceId() {
        return get("resource_id", Integer.class);
    }

    public void setResourceId(Integer resourceId) {
        set("resource_id", resourceId);
    }

    public Integer getResourceAmount() {
        return get("resource_amount", Integer.class);
    }

    public void setResourceAmount(Integer resourceAmount) {
        set("resource_amount", resourceAmount);
    }

    public Timestamp getLastUpdated() {
        return get("last_updated", Timestamp.class);
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        set("last_updated", lastUpdated);
    }
}