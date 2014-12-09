package com.distributed.springtest.client.forms;

import javax.validation.constraints.NotNull;

/**
 * Created by Patrik on 2014-12-09.
 */
public class ResourceForm {

    @NotNull
    private Integer id;

    @NotNull
    private String name;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}