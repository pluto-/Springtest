package com.distributed.springtest.client;

import com.distributed.springtest.client.database.UserAuthentication;
import com.distributed.springtest.client.forms.player.BuildingForm;
import com.distributed.springtest.client.forms.player.BuyBuildingForm;
import com.distributed.springtest.client.forms.player.ConstructionForm;
import com.distributed.springtest.client.forms.player.ResourceForm;
import com.distributed.springtest.utils.exceptions.NotEnoughResourcesException;
import com.distributed.springtest.utils.records.gamecontent.BuildingCost;
import com.distributed.springtest.utils.records.gamecontent.BuildingInfo;
import com.distributed.springtest.utils.records.gamecontent.ResourceInfo;
import com.distributed.springtest.utils.records.playerresources.Building;
import com.distributed.springtest.utils.records.playerresources.Construction;
import com.distributed.springtest.utils.records.playerresources.Resource;
import com.distributed.springtest.utils.wrappers.BuildingInfoWrapper;
import com.distributed.springtest.utils.wrappers.BuyBuildingWrapper;
import com.distributed.springtest.utils.wrappers.PlayerStateWrapper;
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
        ModelAndView modelAndView = new ModelAndView("state");

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
            form.setName(buildingInfo.getName() + " x" + building.getAmount());
            ResourceInfo resourceInfo = restTemplate.getForObject(gamecontentURL + "/resources/" + buildingInfo.getGeneratedId(), ResourceInfo.class);
            form.setGenerates(resourceInfo.getName());
            form.setGeneratedAmount(buildingInfo.getGeneratedAmount() + " x " + building.getAmount() + " (" + (buildingInfo.getGeneratedAmount() * building.getAmount()) + ")");
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

    @RequestMapping("/buy")
    public Object buy() throws SQLException {
        ModelAndView modelAndView = new ModelAndView("player/buy");

        List<BuyBuildingForm> buildings = new ArrayList<>();

        RestTemplate restTemplate = new RestTemplate();
        BuildingInfoWrapper[] buildingInfos = restTemplate.getForObject(gamecontentURL + "/buildingsAndCosts", BuildingInfoWrapper[].class);
        ResourceInfo[] resourceInfos = restTemplate.getForObject(gamecontentURL + "/resources", ResourceInfo[].class);
        Map<Integer, String> idToName = new HashMap<>();
        for(ResourceInfo resourceInfo : resourceInfos) {
            idToName.put(resourceInfo.getId(), resourceInfo.getName());
        }

        modelAndView.addObject("buildings", Arrays.asList(buildingInfos));
        modelAndView.addObject("idToName", idToName);

        return modelAndView;
    }
    @RequestMapping("/buy/{id}")
    public Object buy(@PathVariable Integer id) throws SQLException {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuth = UserAuthentication.select(UserAuthentication.class, "SELECT * FROM user_authentication WHERE username=#1#", username);

        BuyBuildingWrapper wrapper = new BuyBuildingWrapper(userAuth.getPlayerId(), id);

        RestTemplate restTemplate = new RestTemplate();
        ModelAndView modelAndView = new ModelAndView("redirect:/state");


        //ResponseEntity<String> buy = restTemplate.postForEntity(playerResourcesURL + "/building/buy", wrapper, String.class);

        modelAndView.addObject("message", "Hej!");

        return modelAndView;
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public String handleNotEnoughResourcesException(HttpClientErrorException ex, HttpSession session) {

        session.setAttribute("message", ex.getMessage());
        return "redirect:/";

        /*System.err.println("HANDLING");
        ModelAndView model = new ModelAndView("redirect:/");
        redirectAttributes.addFlashAttribute("message", ex.getMessage());
        //model.addObject("message", ex.getMessage());

        return model;*/

    }
}
