package com.distributed.springtest.gamecontent;

import com.distributed.springtest.gamecontent.records.Resource;
import com.distributed.springtest.gamecontent.records.BuildingCost;
import com.distributed.springtest.utils.records.gamecontent.BuildingCostInfo;
import com.distributed.springtest.utils.records.gamecontent.BuildingInfo;
import com.distributed.springtest.utils.records.gamecontent.ResourceInfo;
import com.distributed.springtest.gamecontent.records.Building;
import com.distributed.springtest.utils.wrappers.BuildingInfoWrapper;
import com.jajja.jorm.Record;
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

    @RequestMapping("/buildings")
    public ResponseEntity<List<BuildingInfo>> getBuildings() throws SQLException {
        List<Building> buildings = Record.selectAll(Building.class, "SELECT b.*, r.name as generated_name FROM buildings b, resources r WHERE b.generated_id = r.id");
        List<BuildingInfo> buildingInfoList = new ArrayList<>();
        for(Building building : buildings) {
            BuildingInfo buildingInfo = new BuildingInfo();
            buildingInfo.setId(building.getId());
            buildingInfo.setName(building.getName());
            buildingInfo.setBuildtime(building.getBuildtime());
            buildingInfo.setGeneratedId(building.getGeneratedId());
            buildingInfo.setGeneratedName((String) building.get("generated_name"));
            buildingInfo.setGeneratedAmount(building.getGeneratedAmount());
            buildingInfoList.add(buildingInfo);
        }
        return new ResponseEntity<List<BuildingInfo>>(buildingInfoList, HttpStatus.OK);
    }

    @RequestMapping("/buildings/{id}")
    public ResponseEntity<BuildingInfo> getBuilding(@PathVariable Integer id) throws SQLException {
        Building building = Building.findById(Building.class, id);
        Resource resource = Resource.findById(Resource.class, building.getGeneratedId());
        BuildingInfo buildingInfo = new BuildingInfo();
        buildingInfo.setId(building.getId());
        buildingInfo.setName(building.getName());
        buildingInfo.setBuildtime(building.getBuildtime());
        buildingInfo.setGeneratedId(building.getGeneratedId());
        buildingInfo.setGeneratedName(resource.getName());
        buildingInfo.setGeneratedAmount(building.getGeneratedAmount());
        return new ResponseEntity<BuildingInfo>(buildingInfo, HttpStatus.OK);
    }

    @RequestMapping("/buildings/{id}/costs")
    public ResponseEntity<List<BuildingCostInfo>> getBuildingCost(@PathVariable Integer id) throws SQLException {
        List<BuildingCost> buildingCosts = BuildingCost.selectAll(BuildingCost.class, "SELECT bc.*, r.name as resource_name FROM building_costs bc, resources r WHERE building_id = #1# AND bc.resource_id = r.id", id);
        List<BuildingCostInfo> buildingCostInfoList = new ArrayList<>();
        for(BuildingCost buildingCost : buildingCosts) {
            BuildingCostInfo buildingCostInfo = new BuildingCostInfo();
            buildingCostInfo.setId(buildingCost.getId());
            buildingCostInfo.setResourceId(buildingCost.getResourceId());
            buildingCostInfo.setResourceName((String) buildingCost.get("resource_name"));
            buildingCostInfo.setBuildingId(buildingCost.getBuildingId());
            buildingCostInfo.setAmount(buildingCost.getAmount());
            buildingCostInfoList.add(buildingCostInfo);
        }
        return new ResponseEntity<List<BuildingCostInfo>>(buildingCostInfoList, HttpStatus.OK);
    }

    @RequestMapping(value = "/buildings/{id}/costs/add", method = RequestMethod.POST)
    public ResponseEntity<String> addBuildingCost(@PathVariable Integer id, @RequestBody BuildingCostInfo cost) throws SQLException {
        BuildingCost buildingCost = BuildingCost.select(BuildingCost.class, "SELECT * FROM building_costs WHERE building_id = #1# AND resource_id = #2#", cost.getBuildingId(), cost.getResourceId());
        if(buildingCost == null) {
            buildingCost = new BuildingCost();
            buildingCost.setBuildingId(id);
            buildingCost.setResourceId(cost.getResourceId());
            buildingCost.setAmount(cost.getAmount());
            buildingCost.save();
            buildingCost.transaction().commit();
            return new ResponseEntity<String>("OK", HttpStatus.OK);
        } else {
            return new ResponseEntity<String>("A cost with that resource type already exists", HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/buildings/{id}/costs/{costId}/modify", method = RequestMethod.PUT)
    public void modifyBuildingCost(@PathVariable Integer id, @PathVariable Integer costId, @RequestBody BuildingCostInfo cost) throws SQLException {
        BuildingCost buildingCost = BuildingCost.findById(BuildingCost.class, cost.getId());
        if(buildingCost != null && buildingCost.getBuildingId().equals(id)) {
            buildingCost.setResourceId(cost.getResourceId());
            buildingCost.setAmount(cost.getAmount());
            buildingCost.save();
            buildingCost.transaction().commit();
        }
    }

    @RequestMapping(value = "/buildings/{id}/costs/{costId}/delete", method = RequestMethod.DELETE)
    public void deleteBuildingCost(@PathVariable Integer id, @PathVariable Integer costId) throws SQLException {
        BuildingCost buildingCost  = BuildingCost.findById(BuildingCost.class, costId);
        if(buildingCost != null && buildingCost.getBuildingId().equals(id)) {
            buildingCost.delete();
            buildingCost.transaction().commit();
        }
    }

    @RequestMapping(value = "/buildings/add", method = RequestMethod.POST)
    public ResponseEntity<Integer> addBuilding(@RequestBody BuildingInfo incomingBuildingInfo) throws SQLException {
        Building building = new Building();
        building.setName(incomingBuildingInfo.getName());
        building.setGeneratedId(incomingBuildingInfo.getGeneratedId());
        building.setGeneratedAmount(incomingBuildingInfo.getGeneratedAmount());
        building.setBuildtime(incomingBuildingInfo.getBuildtime());
        building.save();
        building.transaction().commit();
        return new ResponseEntity<Integer>(building.getId(), HttpStatus.OK);
    }

    @RequestMapping(value = "/buildings/modify", method = RequestMethod.PUT)
    public void modifyBuilding(@RequestBody BuildingInfo incomingBuildingInfo) throws SQLException {
        Building building = Building.findById(Building.class, incomingBuildingInfo.getId());
        if(building != null) {
            building.setName(incomingBuildingInfo.getName());
            building.setGeneratedAmount(incomingBuildingInfo.getGeneratedAmount());
            building.setGeneratedId(incomingBuildingInfo.getGeneratedId());
            building.save();
            building.transaction().commit();
        }
    }

    @RequestMapping(value = "/resources/add", method = RequestMethod.POST)
    public Integer addResource(@RequestBody ResourceInfo incomingResourceInfo) throws SQLException {
        Resource resource = new Resource();
        resource.setName(incomingResourceInfo.getName());
        resource.save();
        resource.transaction().commit();
        return resource.getId();
    }

    @RequestMapping("/resources")
    public ResponseEntity<List<ResourceInfo>> getResources() throws SQLException {
        List<Resource> resources = Resource.selectAll(Resource.class, "SELECT * FROM resources");
        List<ResourceInfo> resourceInfoList = new ArrayList<>();
        for(Resource resource : resources) {
            ResourceInfo resourceInfo = new ResourceInfo();
            resourceInfo.setId(resource.getId());
            resourceInfo.setName(resource.getName());
            resourceInfoList.add(resourceInfo);
        }
        return new ResponseEntity<List<ResourceInfo>>(resourceInfoList, HttpStatus.OK);
    }

    @RequestMapping("/resources/{id}")
    public ResponseEntity<ResourceInfo> getResource(@PathVariable Integer id) throws SQLException {
        Resource resource = Resource.select(Resource.class, "SELECT * FROM resources WHERE id = #1#", id);
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setId(resource.getId());
        resourceInfo.setName(resource.getName());
        return new ResponseEntity<ResourceInfo>(resourceInfo, HttpStatus.OK);
    }

    @RequestMapping("/buildingsAndCosts")
    public ResponseEntity<List<BuildingInfoWrapper>> getBuildingsAndCosts() throws SQLException {
        List<BuildingInfoWrapper> resultList = new ArrayList<>();
        List<Building> buildings = Building.selectAll(Building.class, "SELECT b.*, r.name as generated_name FROM buildings b, resources r WHERE b.generated_id = r.id");
        for(Building building : buildings) {
            BuildingInfo buildingInfo = new BuildingInfo();
            buildingInfo.setId(building.getId());
            buildingInfo.setName(building.getName());
            buildingInfo.setBuildtime(building.getBuildtime());
            buildingInfo.setGeneratedId(building.getGeneratedId());
            buildingInfo.setGeneratedName((String) building.get("generated_name"));
            buildingInfo.setGeneratedAmount(building.getGeneratedAmount());
            List<BuildingCost> buildingCosts = BuildingCost.selectAll(BuildingCost.class, "SELECT bc.*, r.name as resource_name FROM building_costs bc, resources r WHERE building_id = #1#", buildingInfo.getId());
            List<BuildingCostInfo> buildingCostInfoList = new ArrayList<>();
            for(BuildingCost buildingCost : buildingCosts) {
                BuildingCostInfo buildingCostInfo = new BuildingCostInfo();
                buildingCostInfo.setId(buildingCost.getId());
                buildingCostInfo.setResourceId(buildingCost.getResourceId());
                buildingCostInfo.setResourceName((String) buildingCost.get("resource_name"));
                buildingCostInfo.setBuildingId(buildingCost.getBuildingId());
                buildingCostInfo.setAmount(buildingCost.getAmount());
                buildingCostInfoList.add(buildingCostInfo);
            }

            resultList.add(new BuildingInfoWrapper(buildingInfo, buildingCostInfoList));
        }
        return new ResponseEntity<List<BuildingInfoWrapper>>(resultList, HttpStatus.OK);
    }
}