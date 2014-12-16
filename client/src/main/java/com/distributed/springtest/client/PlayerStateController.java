package com.distributed.springtest.client;

import com.distributed.springtest.client.database.UserAuthentication;
import com.distributed.springtest.client.forms.player.BuildingForm;
import com.distributed.springtest.client.forms.player.ConstructionForm;
import com.distributed.springtest.client.forms.player.ResourceForm;
import com.distributed.springtest.utils.exceptions.NotEnoughResourcesException;
import com.distributed.springtest.utils.records.gamecontent.BuildingInfo;
import com.distributed.springtest.utils.records.gamecontent.ResourceInfo;
import com.distributed.springtest.utils.records.playerresources.Building;
import com.distributed.springtest.utils.records.playerresources.Construction;
import com.distributed.springtest.utils.records.playerresources.Resource;
import com.distributed.springtest.utils.wrappers.BuildingInfoWrapper;
import com.distributed.springtest.utils.wrappers.BuyBuildingWrapper;
import com.distributed.springtest.utils.wrappers.PlayerStateWrapper;
import com.sun.deploy.net.HttpResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Jonas on 2014-12-11.
 */
@Controller
@RequestMapping("/player")
public class PlayerStateController {

    @Value("${hosts.playerresources}")
    private String playerResourcesURL;

    @Value("${hosts.gamecontent}")
    private String gamecontentURL;

    @RequestMapping("/state")
     public Object state() throws SQLException {
        ModelAndView modelAndView = new ModelAndView("player/state");

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuth = UserAuthentication.select(UserAuthentication.class, "SELECT * FROM user_authentication WHERE username=#1#", username);

        RestTemplate restTemplate = new RestTemplate();
        PlayerStateWrapper wrapper = restTemplate.getForObject(playerResourcesURL + "/state/" + userAuth.getPlayerId(), PlayerStateWrapper.class);

        List<BuildingForm> buildings = new ArrayList<>();
        List<ResourceForm> resources = new ArrayList<>();
        List<ConstructionForm> constructions = new ArrayList<>();

        ResourceInfo[] resourceInfos = restTemplate.getForObject(gamecontentURL + "/resources", ResourceInfo[].class);
        BuildingInfo[] buildingInfos = restTemplate.getForObject(gamecontentURL + "/buildings", BuildingInfo[].class);

        for(Resource resource : wrapper.getResources()) {
            for(ResourceInfo resourceInfo : resourceInfos) {
                if(resourceInfo.getId() == resource.getResourceId()) {
                    ResourceForm form = new ResourceForm();
                    form.setName(resourceInfo.getName());
                    form.setAmount(String.format("%.1f",resource.getAmount()));
                    resources.add(form);
                    break;
                }
            }
        }
        for(Building building : wrapper.getBuildings()) {
            for(BuildingInfo buildingInfo : buildingInfos) {
                if(buildingInfo.getId() == building.getBuildingId()) {
                    BuildingForm form = new BuildingForm();
                    form.setName(buildingInfo.getName() + " x" + building.getAmount());
                    form.setGenerates(buildingInfo.getGeneratedName());
                    form.setGeneratedAmount(buildingInfo.getGeneratedAmount() + " x " + building.getAmount() + " (" + (buildingInfo.getGeneratedAmount() * building.getAmount()) + ")");
                    buildings.add(form);
                    break;
                }
            }
        }
        for(Construction construction : wrapper.getConstructions()) {
            for(BuildingInfo buildingInfo : buildingInfos) {
                if(buildingInfo.getId() == construction.getBuildingId()) {
                    ConstructionForm form = new ConstructionForm();
                    form.setName(buildingInfo.getName());
                    form.setStarted(construction.getStartedAt());
                    form.setFinishes(new Timestamp(construction.getStartedAt().getTime() + buildingInfo.getBuildtime() * 1000));
                    constructions.add(form);

                    break;
                }
            }
        }


        modelAndView.addObject("resources", resources);
        modelAndView.addObject("buildings", buildings);
        modelAndView.addObject("constructions", constructions);

        return modelAndView;
    }

    @RequestMapping("/buy")
    public Object buy() throws SQLException {
        ModelAndView modelAndView = new ModelAndView("player/buy");

        RestTemplate restTemplate = new RestTemplate();
        BuildingInfoWrapper[] buildingInfos = restTemplate.getForObject(gamecontentURL + "/buildingsAndCosts", BuildingInfoWrapper[].class);

        modelAndView.addObject("buildings", Arrays.asList(buildingInfos));

        return modelAndView;
    }

    @RequestMapping("/buy/{id}")
    public Object buy(@PathVariable Integer id, HttpSession session) throws SQLException {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuth = UserAuthentication.select(UserAuthentication.class, "SELECT * FROM user_authentication WHERE username=#1#", username);

        BuyBuildingWrapper wrapper = new BuyBuildingWrapper(userAuth.getPlayerId(), id);

        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView(new RedirectView("/player/buy"));

        ResponseEntity<String> buy = restTemplate.postForEntity(playerResourcesURL + "/building/buy", wrapper, String.class);

        session.setAttribute("message", "Building purchased.");
        return modelAndView;
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public String handleHttpClientErrorException(HttpClientErrorException ex, HttpSession session) {

        try {
            JSONObject json = new JSONObject(ex.getResponseBodyAsString());
            session.setAttribute("message", json.get("message"));
        } catch (JSONException e) {
            session.setAttribute("message", ex.getMessage());
        }

        return "redirect:/";

    }
}
