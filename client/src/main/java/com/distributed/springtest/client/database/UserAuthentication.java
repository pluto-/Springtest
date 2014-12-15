package com.distributed.springtest.client.database;

import com.jajja.jorm.Jorm;
import com.jajja.jorm.Record;

@Jorm(database="login", schema="public", table="user_authentication", primaryKey="player_id")
public class UserAuthentication extends Record {
    public String getUsername() {
        return get("username", String.class);
    }

    public void setUsername(String username) {
        set("username", username);
    }

    public String getPassword() {
        return get("password", String.class);
    }

    public void setPassword(String password) {
        set("password", password);
    }

    public Integer getPlayerId() {
        return get("player_id", Integer.class);
    }

    public void setPlayerId(Integer playerId) {
        set("player_id", playerId);
    }

    public Boolean getEnabled() {
        return get("enabled", Boolean.class);
    }

    public void setEnabled(Boolean enabled) {
        set("enabled", enabled);
    }
}