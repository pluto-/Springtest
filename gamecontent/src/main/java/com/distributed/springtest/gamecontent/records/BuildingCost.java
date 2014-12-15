package com.distributed.springtest.gamecontent.records;

import com.jajja.jorm.Jorm;
import com.jajja.jorm.Record;

@Jorm(database="gamecontent", schema="public", table="building_costs", primaryKey="id")
public class BuildingCost extends Record {
    public Integer getBuildingId() {
        return get("building_id", Integer.class);
    }

    public void setBuildingId(Integer buildingId) {
        set("building_id", buildingId);
    }

    public Integer getResourceId() {
        return get("resource_id", Integer.class);
    }

    public void setResourceId(Integer resourceId) {
        set("resource_id", resourceId);
    }

    public Integer getAmount() {
        return get("amount", Integer.class);
    }

    public void setAmount(Integer amount) {
        set("amount", amount);
    }

    public Integer getId() {
        return get("id", Integer.class);
    }

    public void setId(Integer id) {
        set("id", id);
    }
}
