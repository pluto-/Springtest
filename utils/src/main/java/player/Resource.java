package player;

import com.jajja.jorm.Jorm;
import com.jajja.jorm.Record;

@Jorm(database="player_resources", schema="public", table="resources", primaryKey="player_id")
public class Resource extends Record {
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

    public Integer getAmount() {
        return get("amount", Integer.class);
    }

    public void setAmount(Integer amount) {
        set("amount", amount);
    }
}