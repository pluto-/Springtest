package com.distributed.springtest.records;

import com.jajja.jorm.Jorm;
import com.jajja.jorm.Record;
import java.sql.Timestamp;

@Jorm(database="springtest", schema="public", table="users", primaryKey="id")
public class User extends Record {
    public Integer getId() {
        return get("id", Integer.class);
    }

    public void setId(Integer id) {
        set("id", id);
    }

    public String getName() {
        return get("name", String.class);
    }

    public void setName(String name) {
        set("name", name);
    }

    public Timestamp getCreatedAt() {
        return get("created_at", Timestamp.class);
    }

    public void setCreatedAt(Timestamp createdAt) {
        set("created_at", createdAt);
    }
}