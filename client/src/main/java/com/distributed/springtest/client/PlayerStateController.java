package com.distributed.springtest.client;

import com.distributed.springtest.client.database.UserAuthentication;
import com.distributed.springtest.client.forms.player.BuildingForm;
import com.distributed.springtest.client.forms.player.ConstructionForm;
import com.distributed.springtest.client.forms.player.ResourceForm;
import com.distributed.springtest.utils.records.gamecontent.BuildingInfo;
import com.distributed.springtest.utils.records.gamecontent.ResourceInfo;
import com.distributed.springtest.utils.records.playerresources.Building;
import com.distributed.springtest.utils.records.playerresources.Construction;
import com.distributed.springtest.utils.records.playerresources.Resource;
import com.distributed.springtest.utils.wrappers.PlayerStateWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jonas on 2014-12-11.
 */
@Controller
@RequestMapping("/player/state")
public class PlayerStateController {

    @Value("${hosts.playerresources}")
    private String playerResourcesURL;

    @Value("${hosts.gamecontent}")
    private String gamecontentURL;

    @RequestMapping("")
    public Object state() throws SQLException {
        ModelAndView modelAndView = new ModelAndView("player/state");

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuth = UserAuthentication.select(UserAuthentication.class, "SELECT * FROM user_authentication WHERE username=#1#", username);

        RestTemplate restTemplate = new RestTemplate();
        PlayerStateWrapper wrapper = restTemplate.getForObject(playerResourcesURL + "/state/" + userAuth.getPlayerId(), PlayerStateWrapper.class);

        List<BuildingForm> buildings = new ArrayList<>();
        List<ResourceForm> resources = new ArrayList<>();
        List<ConstructionForm> constructions = new ArrayList<>();

        for(Resource resource : wrapper.getResources()) {
            ResourceInfo resourceInfo = restTemplate.getForObject(gamecontentURL + "/resources/" + resource.getResourceId(), ResourceInfo.class);
            ResourceForm form = new ResourceForm();
            form.setName(resourceInfo.getName());
            form.setAmount(resource.getAmount());
            resources.add(form);
        }
        for(Building building : wrapper.getBuildings()) {
            BuildingInfo buildingInfo = restTemplate.getForObject(gamecontentURL + "/buildings/" + building.getBuildingId(), BuildingInfo.class);
            BuildingForm form = new BuildingForm();
            form.setName(buildingInfo.getName());
            form.setAmount(building.getAmount());
            ResourceInfo resourceInfo = restTemplate.getForObject(gamecontentURL + "/resources/" + buildingInfo.getGeneratedId(), ResourceInfo.class);
            form.setGenerates(resourceInfo.getName());
            form.setGeneratedAmount(buildingInfo.getGeneratedAmount() * form.getAmount());
            buildings.add(form);
        }
        for(Construction construction : wrapper.getConstructions()) {
            BuildingInfo buildingInfo = restTemplate.getForObject(gamecontentURL + "/buildings/" + construction.getBuildingId(), BuildingInfo.class);
            ConstructionForm form = new ConstructionForm();
            form.setName(buildingInfo.getName());
            form.setStarted(construction.getStartedAt());
            form.setFinishes(new Timestamp(construction.getStartedAt().getTime() + buildingInfo.getBuildtime() * 1000));
            constructions.add(form);
        }


        modelAndView.addObject("resources", resources);
        modelAndView.addObject("buildings", buildings);
        modelAndView.addObject("constructions", constructions);

        return modelAndView;
    }
}
