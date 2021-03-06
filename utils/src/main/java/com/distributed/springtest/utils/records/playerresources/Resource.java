package com.distributed.springtest.utils.records.playerresources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jajja.jorm.Jorm;
import com.jajja.jorm.Record;

import com.jajja.jorm.Jorm;
import com.jajja.jorm.Record;

@Jorm(database="player_resources", schema="public", table="resources", primaryKey="id")
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

    public Double getAmount() {
        return get("amount", Double.class);
    }

    public void setAmount(Double amount) {
        set("amount", amount);
    }

    public Integer getId() {
        return get("id", Integer.class);
    }

    public void setId(Integer id) {
        set("id", id);
    }
}