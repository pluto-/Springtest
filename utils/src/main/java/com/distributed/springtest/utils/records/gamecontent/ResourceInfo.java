package com.distributed.springtest.utils.records.gamecontent;

import com.jajja.jorm.Jorm;
import com.jajja.jorm.Record;

@Jorm(database= "com/distributed/springtest/utils/records/gamecontent", schema="public", table="resources", primaryKey="id")
public class ResourceInfo extends Record {
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