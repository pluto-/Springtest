package com.distributed.springtest.client;

import com.distributed.springtest.utils.records.gamecontent.BuildingInfo;
import com.distributed.springtest.utils.records.gamecontent.ResourceInfo;
import com.distributed.springtest.utils.wrappers.PlayerStateWrapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jonas on 2014-12-11.
 */
@Controller
@RequestMapping("/player/state")
public class PlayerStateController {

    @Value("${hosts.playerresources}")
    private String playerResourcesURL;

    @RequestMapping("")
    public Object state() throws SQLException {
        ModelAndView modelAndView = new ModelAndView("player/state");

        RestTemplate restTemplate = new RestTemplate();
        //jonas PlayerStateWrapper wrapper = restTemplate.getForObject(playerResourcesURL + "/state/" + playerId, PlayerStateWrapper.class);
        /*RestTemplate restTemplate = new RestTemplate();
        BuildingInfo[] buildings = restTemplate.getForObject(gamecontentURL + "/buildings", BuildingInfo[].class);
        ResourceInfo[] resourceInfos = restTemplate.getForObject(gamecontentURL + "/resources", ResourceInfo[].class);
        modelAndView.addObject("buildings", buildings);
        Map<Integer, String> resources = new HashMap<>();
        for(ResourceInfo resource : resourceInfos) {
            resources.put(resource.getId(), resource.getName());
        }
        modelAndView.addObject("resources", resources);*/

        return modelAndView;
    }
}
