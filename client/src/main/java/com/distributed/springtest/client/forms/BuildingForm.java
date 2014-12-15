package com.distributed.springtest.client.forms;

import com.distributed.springtest.utils.records.gamecontent.BuildingCostInfo;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patrik on 2014-12-09.
 */
public class BuildingForm {

    private Integer id;

    @NotNull
    private String name;

    @NotNull
    private Integer buildtime;

    @NotNull
    private Integer generatedId;

    @NotNull
    private Float generatedAmount;

    public String getGeneratedName() {
        return generatedName;
    }

    public void setGeneratedName(String generatedName) {
        this.generatedName = generatedName;
    }

    @NotNull
    private String generatedName;

    private List<BuildingCostInfo> buildingCosts = new ArrayList<>();

    public List<BuildingCostInfo> getBuildingCosts() {
        return buildingCosts;
    }

    public void setBuildingCosts(List<BuildingCostInfo> buildingCosts) {
        this.buildingCosts = buildingCosts;
    }

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

    public Integer getBuildtime() {
        return buildtime;
    }

    public void setBuildtime(Integer buildtime) {
        this.buildtime = buildtime;
    }

    public Integer getGeneratedId() {
        return generatedId;
    }

    public void setGeneratedId(Integer generatedId) {
        this.generatedId = generatedId;
    }

    public Float getGeneratedAmount() {
        return generatedAmount;
    }

    public void setGeneratedAmount(Float generatedAmount) {
        this.generatedAmount = generatedAmount;
    }
}