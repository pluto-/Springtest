package com.distributed.springtest.gamecontent;

import com.distributed.springtest.utils.records.gamecontent.BuildingCost;
import com.distributed.springtest.utils.records.gamecontent.BuildingInfo;
import com.distributed.springtest.utils.records.gamecontent.ResourceInfo;
import com.distributed.springtest.utils.records.playerresources.Building;
import com.jajja.jorm.Database;
import com.jajja.jorm.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Patrik on 2014-12-05.
 */
@RestController
public class GameContentController {

    @RequestMapping("/getBuildings")
    public ResponseEntity<List<BuildingInfo>> getBuildings() throws SQLException {
        List<BuildingInfo> buildingInfos = BuildingInfo.selectAll(BuildingInfo.class, "SELECT * FROM buildings");

        return new ResponseEntity<List<BuildingInfo>>(buildingInfos, HttpStatus.OK);
    }

    @RequestMapping("/getBuilding/{id}")
    public ResponseEntity<BuildingInfo> getBuilding(@PathVariable Integer id) throws SQLException {
        BuildingInfo buildingInfo = BuildingInfo.findById(BuildingInfo.class, id);
        return new ResponseEntity<BuildingInfo>(buildingInfo, HttpStatus.OK);
    }

    @RequestMapping("/getBuildingCost/{id}")
    public ResponseEntity<List<BuildingCost>> getBuildingCost(@PathVariable Integer id) throws SQLException {
        List<BuildingCost> buildingCosts = BuildingCost.selectAll(BuildingCost.class, "SELECT * FROM building_costs WHERE building_id = #1#", id);
        return new ResponseEntity<List<BuildingCost>>(buildingCosts, HttpStatus.OK);
    }

    @RequestMapping(value = "/addBuilding", method = RequestMethod.POST)
    public Integer addBuilding(@RequestBody BuildingInfoWrapper buildingInfoWrapper) throws SQLException {
        BuildingInfo incomingBuildingInfo = buildingInfoWrapper.getBuildingInfo();
        BuildingInfo buildingInfo = new BuildingInfo();
        buildingInfo.setName(incomingBuildingInfo.getName());
        buildingInfo.setGeneratedId(incomingBuildingInfo.getGeneratedId());
        buildingInfo.setGeneratedAmount(incomingBuildingInfo.getGeneratedAmount());
        buildingInfo.save();
        List<BuildingCost> buildingCosts = buildingInfoWrapper.getBuildingCosts();
        for(BuildingCost incomingBuildingCost : buildingCosts) {
            BuildingCost buildingCost = new BuildingCost();
            buildingCost.setResourceId(incomingBuildingCost.getResourceId());
            buildingCost.setBuildingId(incomingBuildingCost.getBuildingId());
            buildingCost.setAmount(incomingBuildingCost.getAmount());
            buildingCost.save();
        }
        buildingInfo.transaction().commit();
        return buildingInfo.getId();
    }

    @RequestMapping(value = "/modifyBuilding", method = RequestMethod.PUT)
    public Integer modifyBuilding(@RequestBody BuildingInfoWrapper buildingInfoWrapper) throws SQLException {
        BuildingInfo incomingBuildingInfo = buildingInfoWrapper.getBuildingInfo();
        BuildingInfo buildingInfo = Building.findById(BuildingInfo.class, incomingBuildingInfo.getId());
        buildingInfo.setName(incomingBuildingInfo.getName());
        buildingInfo.setGeneratedAmount(incomingBuildingInfo.getGeneratedAmount());
        buildingInfo.setGeneratedId(incomingBuildingInfo.getGeneratedId());
        buildingInfo.save();
        List<BuildingCost> buildingCosts = buildingInfoWrapper.getBuildingCosts();
        List<BuildingCost> currentBuildingCosts = BuildingCost.selectAll(BuildingCost.class,
                "SELECT * FROM building_costs WHERE building_id = #1#", buildingInfo.getId());
        List<BuildingCost> buildingCostsForRemoval = new ArrayList<BuildingCost>(currentBuildingCosts);
        buildingCostsForRemoval.removeAll(buildingCosts);
        for(BuildingCost buildingCost : buildingCostsForRemoval) {
            buildingCost.delete();
        }
        for(BuildingCost incomingBuildingCost : buildingCosts) {
            BuildingCost buildingCost = BuildingCost.select(BuildingCost.class, "SELECT * FROM building_costs WHERE building_id = #1# AND resource_id = #2#", buildingInfoWrapper.getBuildingInfo().getId(), incomingBuildingCost.getResourceId());
            if(buildingCost == null) {
                buildingCost = new BuildingCost();
            }
            buildingCost.setResourceId(incomingBuildingCost.getResourceId());
            buildingCost.setBuildingId(buildingInfoWrapper.getBuildingInfo().getId());
            buildingCost.setAmount(incomingBuildingCost.getAmount());
            buildingCost.save();
        }
        buildingInfo.transaction().commit();
        return buildingInfo.getId();
    }

    @RequestMapping(value = "/addResource", method = RequestMethod.POST)
    public Integer addResource(@RequestBody ResourceInfo incomingResourceInfo) throws SQLException {
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setName(incomingResourceInfo.getName());
        resourceInfo.save();
        resourceInfo.transaction().commit();
        return resourceInfo.getId();
    }

    @RequestMapping("/getResources")
    public ResponseEntity<List<ResourceInfo>> getResources() throws SQLException {
        List<ResourceInfo> resources = ResourceInfo.selectAll(ResourceInfo.class, "SELECT * FROM resources");
        return new ResponseEntity<List<ResourceInfo>>(resources, HttpStatus.OK);
    }

    @RequestMapping("/getBuildingsAndCosts")
    public ResponseEntity<List<BuildingInfoWrapper>> getBuildingsAndCosts() throws SQLException {
        List<BuildingInfoWrapper> resultList = new LinkedList<BuildingInfoWrapper>();
        List<BuildingInfo> buildingInfoList = BuildingInfo.selectAll(BuildingInfo.class, "SELECT * FROM buildings");
        for(BuildingInfo buildingInfo : buildingInfoList) {
            List<BuildingCost> buildingCosts = BuildingCost.selectAll(BuildingCost.class, "SELECT * FROM building_costs WHERE building_id = #1#", buildingInfo.getId());
            resultList.add(new BuildingInfoWrapper(buildingInfo, buildingCosts));
        }
        return new ResponseEntity<List<BuildingInfoWrapper>>(resultList, HttpStatus.OK);
    }
}