package com.distributed.springtest.gamecontent.records;

import com.jajja.jorm.Jorm;
import com.jajja.jorm.Record;

@Jorm(database="gamecontent", schema="public", table="resources", primaryKey="id")
public class Resource extends Record {
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
}