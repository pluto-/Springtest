package com.distributed.springtest.client.forms.player;

import com.distributed.springtest.utils.records.gamecontent.BuildingCost;
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
    private String generatedAmount;

    /*
    @NotNull
    private Integer amount;

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }*/

    private List<BuildingCost> buildingCosts = ListUtils.lazyList(new ArrayList<BuildingCost>(), FactoryUtils.instantiateFactory(BuildingCost.class));

    public List<BuildingCost> getBuildingCosts() {
        return buildingCosts;
    }

    public void setBuildingCosts(List<BuildingCost> buildingCosts) {
        this.buildingCosts = ListUtils.lazyList(buildingCosts, FactoryUtils.instantiateFactory(BuildingCost.class));
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

    public String getGeneratedAmount() {
        return generatedAmount;
    }

    public void setGeneratedAmount(String generatedAmount) {
        this.generatedAmount = generatedAmount;
    }
}