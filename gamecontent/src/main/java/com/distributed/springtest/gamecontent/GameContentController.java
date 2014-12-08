package com.distributed.springtest.gamecontent;

import com.distributed.springtest.utils.records.gamecontent.Building;
import com.distributed.springtest.utils.records.gamecontent.BuildingCost;
import com.distributed.springtest.utils.records.gamecontent.Resource;
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
    public ResponseEntity<List<Building>> getBuildings() throws SQLException {
        List<Building> buildings = Building.selectAll(Building.class, "SELECT * FROM buildings");

        return new ResponseEntity<List<Building>>(buildings, HttpStatus.OK);
    }

    @RequestMapping("/getBuilding/{id}")
    public ResponseEntity<Building> getBuilding(@PathVariable Integer id) throws SQLException {
        Building building = Building.findById(Building.class, id);
        return new ResponseEntity<Building>(building, HttpStatus.OK);
    }

    @RequestMapping(value = "/addBuilding", method = RequestMethod.POST)
    /*public Object addBuilding(@RequestParam("name") String name, @RequestParam("buildTime") Integer buildTime,
                  @RequestParam("generatedResourceName") String generatedResourceName,
                  @RequestParam("generatedResourceAmount") Integer generatedResourceAmount) throws SQLException {
                  */
    public Integer addBuilding(@RequestBody BuildingWrapper buildingWrapper) throws SQLException {
        Building incomingBuilding = buildingWrapper.getBuilding();
        Building building = new Building();
        building.setName(incomingBuilding.getName());
        building.setGeneratedId(incomingBuilding.getGeneratedId());
        building.setGeneratedAmount(incomingBuilding.getGeneratedAmount());
        building.save();
        List<BuildingCost> buildingCosts = buildingWrapper.getBuildingCosts();
        for(BuildingCost incomingBuildingCost : buildingCosts) {
            BuildingCost buildingCost = new BuildingCost();
            buildingCost.setResourceId(incomingBuildingCost.getResourceId());
            buildingCost.setBuildingId(incomingBuildingCost.getBuildingId());
            buildingCost.setAmount(incomingBuildingCost.getAmount());
            buildingCost.save();
        }
        building.transaction().commit();
        return building.getId();
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
    public Integer addResource(@RequestBody Resource incomingResource) throws SQLException {
        Resource resource = new Resource();
        resource.setName(incomingResource.getName());
        resource.save();
        resource.transaction().commit();
        return resource.getId();
    }
}