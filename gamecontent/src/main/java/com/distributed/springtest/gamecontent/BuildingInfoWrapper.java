package com.distributed.springtest.gamecontent;

import com.distributed.springtest.utils.records.gamecontent.BuildingInfo;
import com.distributed.springtest.utils.records.gamecontent.BuildingCost;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patrik on 2014-12-08.
 */
public class BuildingInfoWrapper {

    private BuildingInfo buildingInfo;
    private List<BuildingCost> buildingCosts;

    public BuildingInfoWrapper() {
        buildingInfo = new BuildingInfo();
        buildingCosts = new ArrayList<BuildingCost>();
    }

    public BuildingInfoWrapper(BuildingInfo buildingInfo, List<BuildingCost> buildingCosts) {
        this.buildingInfo = buildingInfo;
        this.buildingCosts = buildingCosts;
    }

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

    public void addCost(BuildingCost buildingCostInfo) {
        buildingCosts.add(buildingCostInfo);
    }

}
