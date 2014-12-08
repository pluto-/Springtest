package com.distributed.springtest.gamecontent;

import com.distributed.springtest.utils.records.gamecontent.BuildingInfo;
import com.distributed.springtest.utils.records.gamecontent.BuildingCost;

import java.util.List;

/**
 * Created by Patrik on 2014-12-08.
 */
public class BuildingWrapper {

    private BuildingInfo buildingInfo;
    private List<BuildingCost> buildingCosts;

    public BuildingInfo getBuildingInfo() {
        return buildingInfo;
    }

    public void setBuildingInfo(BuildingInfo buildingInfo) {
        this.buildingInfo = buildingInfo;
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
