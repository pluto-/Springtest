package com.distributed.springtest.client;

import com.distributed.springtest.client.forms.BuildingForm;
import com.distributed.springtest.utils.records.gamecontent.BuildingCost;
import com.distributed.springtest.utils.records.gamecontent.BuildingInfo;
import com.distributed.springtest.utils.records.gamecontent.ResourceInfo;
import com.distributed.springtest.utils.records.playerresources.Building;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Patrik on 2014-12-09.
 */
@Controller
@RequestMapping("/buildings")
public class BuildingsController {

    @RequestMapping("")
    public Object buildings() throws SQLException {
        ModelAndView modelAndView = new ModelAndView("buildings");
        List<BuildingInfo> buildings = BuildingInfo.selectAll(BuildingInfo.class, "SELECT * FROM buildings");
        modelAndView.addObject("buildings", buildings);
        return modelAndView;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object getBuilding(@PathVariable Integer id) throws SQLException {
        ModelAndView modelAndView = new ModelAndView("editBuilding");
        BuildingInfo building = BuildingInfo.findById(BuildingInfo.class, id);
        List<BuildingCost> buildingCosts = BuildingCost.selectAll(BuildingCost.class, "SELECT * FROM building_costs b WHERE building_id = #1#", id);
        List<ResourceInfo> resources = ResourceInfo.selectAll(ResourceInfo.class, "SELECT * FROM resources");
        modelAndView.addObject("building", building);
        modelAndView.addObject("buildingCosts", buildingCosts);
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
        building.save();
        building.transaction().commit();
        return new RedirectView("/buildings");
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public Object newBuilding() throws SQLException {
        ModelAndView modelAndView = new ModelAndView("editBuilding");
        List<ResourceInfo> resources = ResourceInfo.selectAll(ResourceInfo.class, "SELECT * FROM resources");
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