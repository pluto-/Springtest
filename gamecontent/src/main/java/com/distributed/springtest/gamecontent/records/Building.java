package com.distributed.springtest.gamecontent.records;

import com.jajja.jorm.Jorm;
import com.jajja.jorm.Record;

import java.util.Map;

@Jorm(database="gamecontent", schema="public", table="buildings", primaryKey="id")
public class Building extends Record {
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

    public Integer getBuildtime() {
        return get("buildtime", Integer.class);
    }

    public void setBuildtime(Integer buildtime) {
        set("buildtime", buildtime);
    }

    public Integer getGeneratedId() {
        return get("generated_id", Integer.class);
    }

    public void setGeneratedId(Integer generatedId) {
        set("generated_id", generatedId);
    }

    public Integer getGeneratedAmount() {
        return get("generated_amount", Integer.class);
    }

    public void setGeneratedAmount(Integer generatedAmount) {
        set("generated_amount", generatedAmount);
    }
}