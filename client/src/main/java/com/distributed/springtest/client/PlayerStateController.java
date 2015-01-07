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
import com.distributed.springtest.utils.security.DigestRestTemplate;
import com.distributed.springtest.utils.wrappers.BuildingInfoWrapper;
import com.distributed.springtest.utils.wrappers.BuyBuildingWrapper;
import com.distributed.springtest.utils.wrappers.PlayerStateWrapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by Jonas on 2014-12-11.
 */
@Controller
@RequestMapping("/player")
public class PlayerStateController implements InitializingBean {

    @Value("${hosts.playerresources}")
    private String playerResourcesURL;

    @Value("${hosts.gamecontent}")
    private String gamecontentURL;

    @Value("${subsystem.username}")
    private String serverUsername;

    @Value("${subsystem.password}")
    private String serverHashedPassword;

    private DigestRestTemplate gameContentRestTemplate;
    private DigestRestTemplate playerResourcesRestTemplate;

    @RequestMapping("/state")
     public Object state() throws SQLException {
        ModelAndView modelAndView = new ModelAndView("player/state");

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuth = UserAuthentication.select(UserAuthentication.class, "SELECT * FROM user_authentication WHERE username=#1#", username);

        ResponseEntity<PlayerStateWrapper> wrapper = playerResourcesRestTemplate.get(playerResourcesURL + "/state/" + userAuth.getPlayerId(), PlayerStateWrapper.class);

        List<BuildingForm> buildings = new ArrayList<>();
        List<ResourceForm> resources = new ArrayList<>();
        List<ConstructionForm> constructions = new ArrayList<>();

        ResponseEntity<ResourceInfo[]> resourceInfos = gameContentRestTemplate.get(gamecontentURL + "/resources", ResourceInfo[].class);
        ResponseEntity<BuildingInfo[]> buildingInfos = gameContentRestTemplate.get(gamecontentURL + "/buildings", BuildingInfo[].class);

        for(Resource resource : wrapper.getBody().getResources()) {
            for(ResourceInfo resourceInfo : resourceInfos.getBody()) {
                if(resourceInfo.getId() == resource.getResourceId()) {
                    ResourceForm form = new ResourceForm();
                    form.setName(resourceInfo.getName());
                    form.setAmount(String.format("%.1f",resource.getAmount()));
                    resources.add(form);
                    break;
                }
            }
        }
        for(Building building : wrapper.getBody().getBuildings()) {
            for(BuildingInfo buildingInfo : buildingInfos.getBody()) {
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
        for(Construction construction : wrapper.getBody().getConstructions()) {
            for(BuildingInfo buildingInfo : buildingInfos.getBody()) {
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

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuth = UserAuthentication.select(UserAuthentication.class, "SELECT * FROM user_authentication WHERE username=#1#", username);

        ResponseEntity<BuildingInfoWrapper[]> buildingInfos = gameContentRestTemplate.get(gamecontentURL + "/buildingsAndCosts", BuildingInfoWrapper[].class);
        ResponseEntity<ResourceInfo[]> resourceInfos = gameContentRestTemplate.get(gamecontentURL + "/resources", ResourceInfo[].class);

        ResponseEntity<Resource[]> resourcesArray = playerResourcesRestTemplate.get(playerResourcesURL + "/" + userAuth.getPlayerId() + "/resources", Resource[].class);
        List<ResourceForm> resources = new ArrayList<>();

        for(Resource resource : resourcesArray.getBody()) {
            for(ResourceInfo resourceInfo : resourceInfos.getBody()) {
                if(resourceInfo.getId() == resource.getResourceId()) {
                    ResourceForm form = new ResourceForm();
                    form.setName(resourceInfo.getName());
                    form.setAmount(String.format("%.1f",resource.getAmount()));
                    resources.add(form);
                    break;
                }
            }
        }

        modelAndView.addObject("buildings", Arrays.asList(buildingInfos.getBody()));
        modelAndView.addObject("resources", resources);

        return modelAndView;
    }

    @RequestMapping("/buy/{id}")
    public Object buy(@PathVariable Integer id, HttpSession session) throws SQLException {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuth = UserAuthentication.select(UserAuthentication.class, "SELECT * FROM user_authentication WHERE username=#1#", username);

        BuyBuildingWrapper wrapper = new BuyBuildingWrapper(userAuth.getPlayerId(), id);

        ModelAndView modelAndView = new ModelAndView(new RedirectView("/player/buy"));

        try {
            ResponseEntity<String> buy = playerResourcesRestTemplate.post(playerResourcesURL + "/building/buy", wrapper, String.class);
            session.setAttribute("message", "Building purchased.");

        } catch(HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.CONFLICT) {
                session.setAttribute("message", e.getResponseBodyAsString());
            } else {
                session.setAttribute("message", e.getMessage());
            }
        }

        return modelAndView;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        gameContentRestTemplate = new DigestRestTemplate(gamecontentURL, serverUsername, serverHashedPassword);
        playerResourcesRestTemplate = new DigestRestTemplate(playerResourcesURL, serverUsername, serverHashedPassword);
    }
}
