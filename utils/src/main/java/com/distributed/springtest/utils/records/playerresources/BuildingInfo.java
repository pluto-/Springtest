package com.distributed.springtest.utils.records.playerresources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by Jonas on 2014-12-05.
 */
public class BuildingInfo {

    public BuildingInfo(int id, String name, int produces_resource_id, int produces_resource_amount) {
        this.id = id;
        this.name = name;
        this.produces_resource_id = produces_resource_id;
        this.produces_resource_amount = produces_resource_amount;
    }

    int id;
    String name;
    int produces_resource_id;
    int produces_resource_amount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProduces_resource_id() {
        return produces_resource_id;
    }

    public void setProduces_resource_id(int produces_resource_id) {
        this.produces_resource_id = produces_resource_id;
    }

    public int getProduces_resource_amount() {
        return produces_resource_amount;
    }

    public void setProduces_resource_amount(int produces_resource_amount) {
        this.produces_resource_amount = produces_resource_amount;
    }


}
