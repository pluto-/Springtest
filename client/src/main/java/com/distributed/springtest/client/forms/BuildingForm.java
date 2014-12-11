package com.distributed.springtest.client.forms;

import com.distributed.springtest.utils.records.gamecontent.BuildingCost;
import com.distributed.springtest.utils.records.gamecontent.BuildingInfo;
import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.ListUtils;
import org.springframework.util.AutoPopulatingList;

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

    private List<BuildingCost> buildingCosts = new ArrayList<>();

    public List<BuildingCost> getBuildingCosts() {
        return buildingCosts;
    }

    public void setBuildingCosts(List<BuildingCost> buildingCosts) {
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