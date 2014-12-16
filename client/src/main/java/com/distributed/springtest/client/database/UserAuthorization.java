package com.distributed.springtest.client.database;

import com.jajja.jorm.Jorm;
import com.jajja.jorm.Record;
import java.sql.SQLException;

@Jorm(database="login", schema="public", table="user_authorization", primaryKey="player_role_id")
public class UserAuthorization extends Record {
    public Integer getPlayerRoleId() {
        return get("player_role_id", Integer.class);
    }

    public void setPlayerRoleId(Integer playerRoleId) {
        set("player_role_id", playerRoleId);
    }

    public Integer getPlayerId() {
        return get("player_id", Integer.class);
    }

    public void setPlayerId(Integer playerId) {
        set("player_id", playerId);
    }

    public UserAuthentication getPlayerRef() throws SQLException {
        return ref("player_id", UserAuthentication.class);
    }

    public void setPlayerRef(UserAuthentication player) {
        set("player_id", player);
    }

    public String getRole() {
        return get("role", String.class);
    }

    public void setRole(String role) {
        set("role", role);
    }
}