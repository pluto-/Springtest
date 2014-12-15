package com.distributed.springtest.utils.records.gamecontent;

public class BuildingInfo{

    private Integer id;
    private String name;
    private Integer buildtime;
    private Integer generatedId;
    private String generatedName;

    public String getGeneratedName() {
        return generatedName;
    }

    public void setGeneratedName(String generatedName) {
        this.generatedName = generatedName;
    }

    private Float generatedAmount;

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