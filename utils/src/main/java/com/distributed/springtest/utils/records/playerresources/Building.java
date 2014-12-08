package com.distributed.springtest.utils.records.playerresources;

import com.jajja.jorm.Jorm;
import com.jajja.jorm.Record;
import java.sql.Timestamp;

@Jorm(database="player_resources", schema="public", table="buildings", primaryKey="player_id")
public class Building extends Record {
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

    public Timestamp getLastUpdated() {
        return get("last_updated", Timestamp.class);
    }

    public void setLastUpdated(Timestamp lastUpdated) {
        set("last_updated", lastUpdated);
    }

    public Integer getAmount() {
        return get("amount", Integer.class);
    }

    public void setAmount(Integer amount) {
        set("amount", amount);
    }
}