package com.distributed.springtest.client;

import com.distributed.springtest.client.forms.BuildingForm;
import com.distributed.springtest.utils.records.gamecontent.BuildingCost;
import com.distributed.springtest.utils.records.gamecontent.BuildingInfo;
import com.distributed.springtest.utils.records.gamecontent.ResourceInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
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
@RequestMapping("/buildings")
public class BuildingsController {

    @Value("${hosts.gamecontent}")
    private String gamecontentURL;

    @RequestMapping("")
    public Object buildings() throws SQLException {
        ModelAndView modelAndView = new ModelAndView("buildings");
        RestTemplate restTemplate = new RestTemplate();
        BuildingInfo[] buildings = restTemplate.getForObject(gamecontentURL + "/buildings", BuildingInfo[].class);
        ResourceInfo[] resourceInfos = restTemplate.getForObject(gamecontentURL + "/resources", ResourceInfo[].class);
        modelAndView.addObject("buildings", buildings);
        Map<Integer, String> resources = new HashMap<>();
        for(ResourceInfo resource : resourceInfos) {
            resources.put(resource.getId(), resource.getName());
        }
        modelAndView.addObject("resources", resources);

        return modelAndView;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object getBuilding(@PathVariable Integer id) throws SQLException {
        ModelAndView modelAndView = new ModelAndView("editBuilding");
        RestTemplate restTemplate = new RestTemplate();
        BuildingInfo building = restTemplate.getForObject(gamecontentURL + "/buildings/" + id, BuildingInfo.class);
        BuildingCost[] buildingCosts = restTemplate.getForObject(gamecontentURL + "/buildings/" + id + "/costs", BuildingCost[].class);
        ResourceInfo[] resourceInfos = restTemplate.getForObject(gamecontentURL + "/resources", ResourceInfo[].class);
        Map<Integer, String> resources = new HashMap<>();
        for(ResourceInfo resource : resourceInfos) {
            resources.put(resource.getId(), resource.getName());
        }
        BuildingForm form = new BuildingForm();
        form.setGeneratedAmount(building.getGeneratedAmount());
        form.setGeneratedId(building.getGeneratedId());
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
        BuildingInfo building = BuildingInfo.findById(BuildingInfo.class, id);
        building.setName(form.getName());
        building.setBuildtime(form.getBuildtime());
        building.setGeneratedId(form.getGeneratedId());
        building.setGeneratedAmount(form.getGeneratedAmount());
        //TODO handle new buildingcosts
        building.save();
        building.transaction().commit();
        return new RedirectView("/buildings");
    }

    @RequestMapping(value = "/{id}/addCost", method = RequestMethod.POST)
    public Object addCost(@PathVariable Integer id, @RequestParam Integer newCostResourceId, @RequestParam Integer newCostAmount) {
        BuildingCost cost = new BuildingCost();
        cost.setBuildingId(id);
        cost.setResourceId(newCostResourceId);
        cost.setAmount(newCostAmount);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(gamecontentURL + "/buildings/" + id + "/costs/add", cost, String.class);
        System.out.println(responseEntity.getStatusCode() + ": " + responseEntity.getBody());
        return new RedirectView("/buildings/" + id, true);
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public Object newBuilding() throws SQLException {
        ModelAndView modelAndView = new ModelAndView("editBuilding");
        RestTemplate restTemplate = new RestTemplate();
        ResourceInfo[] resources = restTemplate.getForObject(gamecontentURL + "/resources", ResourceInfo[].class);
        modelAndView.addObject("edit", false);
        modelAndView.addObject("resources", resources);
        return modelAndView;
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public Object newBuildingx(@ModelAttribute @Valid BuildingForm form, BindingResult result) throws SQLException {
        BuildingInfo building = new BuildingInfo();
        building.setName(form.getName());
        building.setBuildtime(form.getBuildtime());
        building.setGeneratedId(form.getGeneratedId());
        building.setGeneratedAmount(form.getGeneratedAmount());
        building.save();
        building.transaction().commit();
        return new RedirectView("/buildings");
    }
}