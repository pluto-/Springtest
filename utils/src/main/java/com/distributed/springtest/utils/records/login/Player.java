package com.distributed.springtest.utils.records.login;

import com.jajja.jorm.Jorm;
import com.jajja.jorm.Record;

@Jorm(database="login", schema="public", table="players", primaryKey="id")
public class Player extends Record {
    public Integer getId() {
        return get("id", Integer.class);
    }

    public void setId(Integer id) {
        set("id", id);
    }

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
}