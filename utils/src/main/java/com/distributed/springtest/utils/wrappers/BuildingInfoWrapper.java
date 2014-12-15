package com.distributed.springtest.utils.wrappers;

import com.distributed.springtest.utils.records.gamecontent.BuildingInfo;
import com.distributed.springtest.utils.records.gamecontent.BuildingCostInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patrik on 2014-12-08.
 */
public class BuildingInfoWrapper {

    private BuildingInfo buildingInfo;
    private List<BuildingCostInfo> buildingCosts;

    public BuildingInfoWrapper() {
        buildingInfo = new BuildingInfo();
        buildingCosts = new ArrayList<BuildingCostInfo>();
    }

    public BuildingInfoWrapper(BuildingInfo buildingInfo, List<BuildingCostInfo> buildingCosts) {
        this.buildingInfo = buildingInfo;
        this.buildingCosts = buildingCosts;
    }

    public BuildingInfo getBuildingInfo() {
        return buildingInfo;
    }

    public void setBuildingInfo(BuildingInfo buildingInfo) {
        this.buildingInfo = buildingInfo;
    }

    public List<BuildingCostInfo> getBuildingCosts() {
        return buildingCosts;
    }

    public void setBuildingCosts(List<BuildingCostInfo> buildingCosts) {
        this.buildingCosts = buildingCosts;
    }

    public void addCost(BuildingCostInfo buildingCostInfo) {
        buildingCosts.add(buildingCostInfo);
    }

}
