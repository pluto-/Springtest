package com.distributed.springtest.client;

import com.distributed.springtest.client.database.UserAuthentication;
import com.distributed.springtest.client.forms.player.AuctionForm;
import com.distributed.springtest.utils.records.gamecontent.ResourceInfo;
import com.distributed.springtest.utils.records.playerresources.Resource;
import com.distributed.springtest.utils.security.DigestRestTemplate;
import com.distributed.springtest.utils.wrappers.AuctionWrapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Patrik on 2014-12-16.
 */
@Controller
@RequestMapping("/player/trading")
public class AuctionController implements InitializingBean {

    @Value("${hosts.auction}")
    private String auctionURL;
    @Value("${hosts.gamecontent}")
    private String gamecontentURL;
    @Value("${hosts.playerresources}")
    private String playerResourcesURL;

    @Value("${subsystem.username}")
    private String serverUsername;

    @Value("${subsystem.password}")
    private String serverHashedPassword;

    private DigestRestTemplate gameContentRestTemplate;
    private DigestRestTemplate playerResourcesRestTemplate;
    private DigestRestTemplate auctionRestTemplate;

    @RequestMapping("")
    public Object getAuctions() {
        List<AuctionWrapper> auctions = Arrays.asList(auctionRestTemplate.get(auctionURL, AuctionWrapper[].class).getBody());
        ModelAndView modelAndView = new ModelAndView("player/auctions");
        modelAndView.addObject("auctions", auctions);
        return modelAndView;
    }

    @RequestMapping(value = "/buy/{id}", method = RequestMethod.POST)
    public Object buy(@PathVariable Integer id) throws SQLException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuth = UserAuthentication.select(UserAuthentication.class, "SELECT * FROM user_authentication WHERE username=#1#", username);
        auctionRestTemplate.get(auctionURL + "/" + userAuth.getPlayerId() + "/buy/" + id, String.class);
        return new RedirectView("/player/trading");
    }

    @RequestMapping(value = "/new", method = RequestMethod.GET)
    public Object newAuction() throws SQLException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuth = UserAuthentication.select(UserAuthentication.class, "SELECT * FROM user_authentication WHERE username=#1#", username);
        List<ResourceInfo> resourceInfos = Arrays.asList(gameContentRestTemplate.get(gamecontentURL + "/resources", ResourceInfo[].class).getBody());
        Map<Integer, String> resources = new HashMap<>();
        for(ResourceInfo resource : resourceInfos) {
            resources.put(resource.getId(), resource.getName());
        }
        List<Resource> playerResources = Arrays.asList(playerResourcesRestTemplate.get(playerResourcesURL + "/" + userAuth.getPlayerId() +  "/resources", Resource[].class).getBody());
        ModelAndView modelAndView = new ModelAndView("player/auctionNew");
        modelAndView.addObject("resources", resources);
        modelAndView.addObject("playerResources", playerResources);
        return modelAndView;
    }

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public Object newAuctionx(@ModelAttribute @Valid AuctionForm form, BindingResult result) throws SQLException {
        if(result.hasErrors()) {
            StringBuilder message = new StringBuilder();
            for(FieldError error: result.getFieldErrors()) {
                message.append(error.getField()).append(" - ").append(error.getRejectedValue()).append("\n");
            }
            ModelAndView modelAndView = (ModelAndView)newAuction();
            modelAndView.addObject("message", message);
            modelAndView.addObject("form", form);
            return modelAndView;
        }
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuth = UserAuthentication.select(UserAuthentication.class, "SELECT * FROM user_authentication WHERE username=#1#", username);
        AuctionWrapper wrapper = new AuctionWrapper();
        wrapper.setDemandResourceId(form.getDemandedResourceId());
        wrapper.setDemandAmount(form.getDemandedAmount());
        wrapper.setOfferResourceId(form.getOfferedResourceId());
        wrapper.setOfferAmount(form.getOfferedAmount());
        wrapper.setSellerId(userAuth.getPlayerId());
        ResponseEntity<String> responseEntity = auctionRestTemplate.post(auctionURL + "/new", wrapper, String.class);
        return new RedirectView("/player/trading");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        gameContentRestTemplate = new DigestRestTemplate(gamecontentURL, serverUsername, serverHashedPassword);
        playerResourcesRestTemplate = new DigestRestTemplate(playerResourcesURL, serverUsername, serverHashedPassword);
        auctionRestTemplate = new DigestRestTemplate(auctionURL, serverUsername, serverHashedPassword);
    }
}
