package com.distributed.springtest.client.forms.player;

import com.distributed.springtest.utils.records.gamecontent.BuildingCostInfo;
import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.ListUtils;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patrik on 2014-12-09.
 */
public class BuildingForm {

    @NotNull
    private String name;

    @NotNull
    private String generates;

    @NotNull
    private Float generatedAmount;

    @NotNull
    private Integer amount;

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    private List<BuildingCostInfo> buildingCosts = ListUtils.lazyList(new ArrayList<BuildingCostInfo>(), FactoryUtils.instantiateFactory(BuildingCostInfo.class));

    public List<BuildingCostInfo> getBuildingCosts() {
        return buildingCosts;
    }

    public void setBuildingCosts(List<BuildingCostInfo> buildingCosts) {
        this.buildingCosts = ListUtils.lazyList(buildingCosts, FactoryUtils.instantiateFactory(BuildingCostInfo.class));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenerates() {
        return generates;
    }

    public void setGenerates(String generates) {
        this.generates = generates;
    }

    public Float getGeneratedAmount() {
        return generatedAmount;
    }

    public void setGeneratedAmount(Float generatedAmount) {
        this.generatedAmount = generatedAmount;
    }
}