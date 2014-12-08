package com.distributed.springtest.gamecontent;

import com.distributed.springtest.utils.records.gamecontent.BuildingInfo;
import com.distributed.springtest.utils.records.gamecontent.BuildingCost;
import com.distributed.springtest.utils.records.gamecontent.ResourceInfo;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
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

    @RequestMapping(value = "/addBuilding", method = RequestMethod.POST)
    /*public Object addBuilding(@RequestParam("name") String name, @RequestParam("buildTime") Integer buildTime,
                  @RequestParam("generatedResourceName") String generatedResourceName,
                  @RequestParam("generatedResourceAmount") Integer generatedResourceAmount) throws SQLException {
                  */
    public Integer addBuilding(@RequestBody BuildingWrapper buildingWrapper) throws SQLException {
        BuildingInfo incomingBuildingInfo = buildingWrapper.getBuildingInfo();
        BuildingInfo buildingInfo = new BuildingInfo();
        buildingInfo.setName(incomingBuildingInfo.getName());
        buildingInfo.setGeneratedId(incomingBuildingInfo.getGeneratedId());
        buildingInfo.setGeneratedAmount(incomingBuildingInfo.getGeneratedAmount());
        buildingInfo.save();
        List<BuildingCost> buildingCosts = buildingWrapper.getBuildingCosts();
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

    @RequestMapping("/setBuildingCost")
    public Object setBuildingCost(@RequestParam("buildingId") Integer buildingId, @RequestParam("resourceId") Integer resourceId, @RequestParam("amount") Integer amount) throws SQLException {
        BuildingCost buildingCost = new BuildingCost();
        buildingCost.setBuildingId(buildingId);
        buildingCost.setAmount(amount);
        buildingCost.setResourceId(resourceId);
        return "OK";
    }

    @RequestMapping("/addResource")
    public Integer addResource(@RequestBody ResourceInfo incomingResourceInfo) throws SQLException {
        ResourceInfo resourceInfo = new ResourceInfo();
        resourceInfo.setName(incomingResourceInfo.getName());
        resourceInfo.save();
        resourceInfo.transaction().commit();
        return resourceInfo.getId();
    }
}