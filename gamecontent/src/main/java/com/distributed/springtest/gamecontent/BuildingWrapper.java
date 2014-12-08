package com.distributed.springtest.gamecontent;

import com.distributed.springtest.gamecontent.records.Building;
import com.distributed.springtest.gamecontent.records.BuildingCost;

import java.util.List;

/**
 * Created by Patrik on 2014-12-08.
 */
public class BuildingWrapper {

    private Building building;
    private List<BuildingCost> buildingCosts;

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public List<BuildingCost> getBuildingCosts() {
        return buildingCosts;
    }

    public void setBuildingCosts(List<BuildingCost> buildingCosts) {
        this.buildingCosts = buildingCosts;
    }

    public void addCost(BuildingCost buildingCost) {
        buildingCosts.add(buildingCost);
    }
}
