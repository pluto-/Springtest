package com.distributed.springtest.utils.records.playerresources;
import com.jajja.jorm.Jorm;
import com.jajja.jorm.Record;
import java.sql.Timestamp;

@Jorm(database="player_resources", schema="public", table="construction", primaryKey="id")
public class Construction extends Record {
    public Integer getId() {
        return get("id", Integer.class);
    }

    public void setId(Integer id) {
        set("id", id);
    }

    public Integer getPlayerId() {
        return get("player_id", Integer.class);
    }

    public void setPlayerId(Integer playerId) {
        set("player_id", playerId);
    }

    public Integer getBuildingId() {
        return get("building_id", Integer.class);
    }

    public void setBuildingId(Integer buildingId) {
        set("building_id", buildingId);
    }

    public Timestamp getStartedAt() {
        return get("started_at", Timestamp.class);
    }

    public void setStartedAt(Timestamp startedAt) {
        set("started_at", startedAt);
    }
}
