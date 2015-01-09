package com.distributed.springtest.client;

import com.distributed.springtest.client.forms.BuildingForm;
import com.distributed.springtest.utils.records.gamecontent.BuildingCostInfo;
import com.distributed.springtest.utils.records.gamecontent.BuildingInfo;
import com.distributed.springtest.utils.records.gamecontent.ResourceInfo;
import com.distributed.springtest.utils.security.DigestRestTemplate;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.*;

/**
 * Controller for the buildings administration part of the client site
 */
@Controller
@RequestMapping("/admin/buildings")
public class BuildingsController implements InitializingBean {

    @Value("${hosts.gamecontent}")
    private String gamecontentURL;

    @Value("${subsystem.username}")
    private String serverUsername;

    @Value("${subsystem.password}")
    private String serverHashedPassword;

    private DigestRestTemplate gameContentRestTemplate;

    /**
     * Retrieves information about all available building types from the game content subsystem.
     * @return a model and view containing information about all available building types.
     * @throws SQLException
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Object buildings() throws SQLException {
        ModelAndView modelAndView = new ModelAndView("admin/buildings");
        ResponseEntity<BuildingInfo[]> buildings = gameContentRestTemplate.get(gamecontentURL + "/buildings", BuildingInfo[].class);
        modelAndView.addObject("buildings", buildings.getBody());
        return modelAndView;
    }

    /**
     * Retreives information about a specified building type and any associated building costs from the game content subsystem.
     * @param id id of the specified building
     * @return model and view containing the retrieved information
     * @throws SQLException
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Object getBuilding(@PathVariable Integer id) throws SQLException {
        ModelAndView modelAndView = new ModelAndView("admin/editBuilding");
        ResponseEntity<BuildingInfo> building = gameContentRestTemplate.get(gamecontentURL + "/buildings/" + id, BuildingInfo.class);
        ResponseEntity<BuildingCostInfo[]> buildingCosts = gameContentRestTemplate.get(gamecontentURL + "/buildings/" + id + "/costs", BuildingCostInfo[].class);
        ResponseEntity<ResourceInfo[]> resourceInfos = gameContentRestTemplate.get(gamecontentURL + "/resources", ResourceInfo[].class);
        Map<Integer, String> resources = new HashMap<>();
        for(ResourceInfo resource : resourceInfos.getBody()) {
            resources.put(resource.getId(), resource.getName());
        }
        BuildingForm form = new BuildingForm();
        form.setGeneratedAmount(building.getBody().getGeneratedAmount());
        form.setGeneratedId(building.getBody().getGeneratedId());
        form.setGeneratedName(building.getBody().getGeneratedName());
        form.setName(building.getBody().getName());
        form.setId(building.getBody().getId());
        form.setBuildtime(building.getBody().getBuildtime());
        form.setBuildingCosts(Arrays.asList(buildingCosts.getBody()));
        modelAndView.addObject("form", form);
        modelAndView.addObject("resources", resources);
        modelAndView.addObject("edit", true);
        return modelAndView;
    }

    /**
     * Modifies a specified building type
     * @param id id of the specified building
     * @param form backing object containing updated information about the building type
     * @param result result of the validation of the backing object
     * @return redirection to the main building administration page
     * @throws SQLException
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public Object editBuilding(@PathVariable Integer id, @ModelAttribute @Valid BuildingForm form, BindingResult result) throws SQLException {
        BuildingInfo building = new BuildingInfo();
        building.setId(id);
        building.setName(form.getName());
        building.setBuildtime(form.getBuildtime());
        building.setGeneratedId(form.getGeneratedId());
        building.setGeneratedAmount(form.getGeneratedAmount());
        gameContentRestTemplate.put(gamecontentURL + "/buildings/modify", building);
        return new RedirectView("/admin/buildings", true);
    }

    /**
     * Adds a building cost to a specified building type
     * @param id id of the specified building type
     * @param newCostResourceId resource id for the new cost
     * @param newCostAmount the amount of the new cost
     * @return redirection to the building details page of the specified building
     */
    @RequestMapping(value = "/{id}/addCost", method = RequestMethod.POST)
    public Object addCost(@PathVariable Integer id, @RequestParam Integer newCostResourceId, @RequestParam Integer newCostAmount) {
        System.out.println(id + " " + newCostResourceId + ":" + newCostAmount);
        BuildingCostInfo cost = new BuildingCostInfo();
        cost.setId(1);
        cost.setBuildingId(id);
        cost.setResourceId(newCostResourceId);
        cost.setAmount(newCostAmount);
        System.out.println(cost.getId() + ":" + cost.getBuildingId() + "-" + cost.getResourceId() + "-" + cost.getAmount());
        gameContentRestTemplate.post(gamecontentURL + "/buildings/" + id + "/costs/add", cost, String.class);
        return new RedirectView("/admin/buildings/" + id, true);
    }

    /**
     * Modifies a specified building cost of a specified building type.
     * @param id id of the specified building type
     * @param costId id of the specified cost
     * @param resourceId the new resource id of the cost
     * @param amount the new amount of the cost
     * @return redirection to the building details page of the specified building
     */
    @RequestMapping(value = "/{id}/modifyCost/{costId}", method = RequestMethod.POST)
    public Object modifyCost(@PathVariable Integer id, @PathVariable Integer costId, @RequestParam Integer resourceId, @RequestParam Integer amount) {
        BuildingCostInfo cost = new BuildingCostInfo();
        cost.setId(costId);
        cost.setBuildingId(id);
        cost.setResourceId(resourceId);
        cost.setAmount(amount);
        System.out.println(cost.getId() + " - " + cost.getBuildingId() + " : " + cost.getResourceId() + " - " + cost.getAmount());
        gameContentRestTemplate.put(gamecontentURL + "/buildings/" + id + "/costs/" + costId + "/modify", cost);
        return new RedirectView("/admin/buildings/" + id, true);
    }

    /**
     * Removes a specified building cost from a specified building type
     * @param id id of the building type
     * @param costId id of the cost
     * @return redirection to the building details page of the specified building
     */
    @RequestMapping(value = "/{id}/removeCost/{costId}")
    public Object removeCost(@PathVariable Integer id, @PathVariable Integer costId) {
        gameContentRestTemplate.delete(gamecontentURL + "/buildings/" + id + "/costs/" + costId + "/delete");
        return new RedirectView("/admin/buildings/" + id, true);
    }

    /**
     * Generates a page for creating a new building type
     * @return model and view containing all available resource types
     * @throws SQLException
     */
    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public Object newBuilding() throws SQLException {
        ModelAndView modelAndView = new ModelAndView("admin/editBuilding");
        ResponseEntity<ResourceInfo[]> resourceInfos = gameContentRestTemplate.get(gamecontentURL + "/resources", ResourceInfo[].class);
        Map<Integer,String> resources = new HashMap<>();
        for(ResourceInfo resourceInfo : resourceInfos.getBody()) {
            resources.put(resourceInfo.getId(), resourceInfo.getName());
        }
        modelAndView.addObject("edit", false);
        modelAndView.addObject("resources", resources);
        return modelAndView;
    }

    /**
     * Creates a new building type
     * @param form backing object containing information about the new building type
     * @param result result of the validation of the backing object
     * @return Redirection to the building details page of the created building type.
     * @throws SQLException
     */
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public Object newBuildingx(@ModelAttribute @Valid BuildingForm form, BindingResult result) throws SQLException {
        BuildingInfo building = new BuildingInfo();
        building.setId(1);
        building.setName(form.getName());
        building.setBuildtime(form.getBuildtime());
        building.setGeneratedId(form.getGeneratedId());
        building.setGeneratedAmount(form.getGeneratedAmount());
        ResponseEntity<Integer> response = gameContentRestTemplate.post(gamecontentURL + "/buildings/add", building, Integer.class);
        return new RedirectView("/admin/buildings/" + response.getBody(), true);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        gameContentRestTemplate = new DigestRestTemplate(gamecontentURL, serverUsername, serverHashedPassword);
    }
}