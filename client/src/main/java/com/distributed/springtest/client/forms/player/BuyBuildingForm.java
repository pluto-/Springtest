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
public class BuyBuildingForm {

    @NotNull
    private String name;

    @NotNull
    private String generates;

    @NotNull
    private String generatedAmount;

    @NotNull
    private BuildingCost[] costs;

    public BuildingCost[] getCosts() {
        return costs;
    }

    public void setCosts(BuildingCost[] costs) {
        this.costs = costs;
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