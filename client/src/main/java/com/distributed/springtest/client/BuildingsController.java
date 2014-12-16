package com.distributed.springtest.client;

import com.distributed.springtest.client.forms.BuildingForm;
import com.distributed.springtest.utils.records.gamecontent.BuildingCostInfo;
import com.distributed.springtest.utils.records.gamecontent.BuildingInfo;
import com.distributed.springtest.utils.records.gamecontent.ResourceInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Patrik on 2014-12-09.
 */
@Controller
@RequestMapping("/admin/buildings")
public class BuildingsController {

    @Value("${hosts.gamecontent}")
    private String gamecontentURL;

    @RequestMapping("")
    public Object buildings() throws SQLException {
        ModelAndView modelAndView = new ModelAndView("admin/buildings");
        RestTemplate restTemplate = new RestTemplate();
        BuildingInfo[] buildings = restTemplate.getForObject(gamecontentURL + "/buildings", BuildingInfo[].class);
        modelAndView.addObject("buildings", buildings);
        return modelAndView;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object getBuilding(@PathVariable Integer id) throws SQLException {
        ModelAndView modelAndView = new ModelAndView("admin/editBuilding");
        RestTemplate restTemplate = new RestTemplate();
        BuildingInfo building = restTemplate.getForObject(gamecontentURL + "/buildings/" + id, BuildingInfo.class);
        BuildingCostInfo[] buildingCosts = restTemplate.getForObject(gamecontentURL + "/buildings/" + id + "/costs", BuildingCostInfo[].class);
        ResourceInfo[] resourceInfos = restTemplate.getForObject(gamecontentURL + "/resources", ResourceInfo[].class);
        Map<Integer, String> resources = new HashMap<>();
        for(ResourceInfo resource : resourceInfos) {
            resources.put(resource.getId(), resource.getName());
        }
        BuildingForm form = new BuildingForm();
        form.setGeneratedAmount(building.getGeneratedAmount());
        form.setGeneratedId(building.getGeneratedId());
        form.setGeneratedName(building.getGeneratedName());
        form.setName(building.getName());
        form.setId(building.getId());
        form.setBuildtime(building.getBuildtime());
        form.setBuildingCosts(Arrays.asList(buildingCosts));
        modelAndView.addObject("form", form);
        modelAndView.addObject("resources", resources);
        modelAndView.addObject("edit", true);
        return modelAndView;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public Object editBuilding(@PathVariable Integer id, @ModelAttribute @Valid BuildingForm form, BindingResult result) throws SQLException {
        BuildingInfo building = new BuildingInfo();
        building.setId(id);
        building.setName(form.getName());
        building.setBuildtime(form.getBuildtime());
        building.setGeneratedId(form.getGeneratedId());
        building.setGeneratedAmount(form.getGeneratedAmount());
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(gamecontentURL + "/buildings/modify", building);
        return new RedirectView("/admin/buildings", true);
    }

    @RequestMapping(value = "/{id}/addCost", method = RequestMethod.POST)
    public Object addCost(@PathVariable Integer id, @RequestParam Integer newCostResourceId, @RequestParam Integer newCostAmount) {
        System.out.println(id + " " + newCostResourceId + ":" + newCostAmount);
        BuildingCostInfo cost = new BuildingCostInfo();
        cost.setId(1);
        cost.setBuildingId(id);
        cost.setResourceId(newCostResourceId);
        cost.setAmount(newCostAmount);
        RestTemplate restTemplate = new RestTemplate();
        System.out.println(cost.getId() + ":" + cost.getBuildingId() + "-" + cost.getResourceId() + "-" + cost.getAmount());
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(gamecontentURL + "/buildings/" + id + "/costs/add", cost, String.class);
        System.out.println(responseEntity.getStatusCode() + ": " + responseEntity.getBody());
        return new RedirectView("/admin/buildings/" + id, true);
    }

    @RequestMapping(value = "/{id}/modifyCost/{costId}", method = RequestMethod.POST)
    public Object modifyCost(@PathVariable Integer id, @PathVariable Integer costId, @RequestParam Integer resourceId, @RequestParam Integer amount) {
        BuildingCostInfo cost = new BuildingCostInfo();
        cost.setId(costId);
        cost.setBuildingId(id);
        cost.setResourceId(resourceId);
        cost.setAmount(amount);
        System.out.println(cost.getId() + " - " + cost.getBuildingId() + " : " + cost.getResourceId() + " - " + cost.getAmount());
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(gamecontentURL + "/buildings/" + id + "/costs/" + costId + "/modify", cost);
        return new RedirectView("/admin/buildings/" + id, true);
    }

    @RequestMapping(value = "/{id}/removeCost/{costId}")
    public Object removeCost(@PathVariable Integer id, @PathVariable Integer costId) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.delete(gamecontentURL + "/buildings/" + id + "/costs/" + costId + "/delete");
        return new RedirectView("/admin/buildings/" + id, true);
    }


    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public Object newBuilding() throws SQLException {
        ModelAndView modelAndView = new ModelAndView("editBuilding");
        RestTemplate restTemplate = new RestTemplate();
        ResourceInfo[] resourceInfos = restTemplate.getForObject(gamecontentURL + "/resources", ResourceInfo[].class);
        Map<Integer,String> resources = new HashMap<>();
        for(ResourceInfo resourceInfo : resourceInfos) {
            resources.put(resourceInfo.getId(), resourceInfo.getName());
        }
        modelAndView.addObject("edit", false);
        modelAndView.addObject("resources", resources);
        return modelAndView;
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public Object newBuildingx(@ModelAttribute @Valid BuildingForm form, BindingResult result) throws SQLException {
        BuildingInfo building = new BuildingInfo();
        building.setId(1);
        building.setName(form.getName());
        building.setBuildtime(form.getBuildtime());
        building.setGeneratedId(form.getGeneratedId());
        building.setGeneratedAmount(form.getGeneratedAmount());
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Integer> responseEntity = restTemplate.postForEntity(gamecontentURL + "/buildings/add", building, Integer.class);
        return new RedirectView("/buildings/" + responseEntity.getBody(), true);
    }
}