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
 * Controller for the auction part of the client site.
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

    /**
     * Retreives all available auctions from the auction subsystem and builds a model and view to be presented to the user.
     * @return the model and view to be presented to the user.
     */
    @RequestMapping("")
    public Object getAuctions() {
        List<AuctionWrapper> auctions = Arrays.asList(auctionRestTemplate.get(auctionURL, AuctionWrapper[].class).getBody());
        ModelAndView modelAndView = new ModelAndView("player/auctions");
        modelAndView.addObject("auctions", auctions);
        return modelAndView;
    }

    /**
     * Buys an auction, provided the user has enough of the requested resource, redirects the user back to the main auction page.
     * @param id id of the auction
     * @return redirect to the main auction page
     * @throws SQLException
     */
    @RequestMapping(value = "/buy/{id}", method = RequestMethod.POST)
    public Object buy(@PathVariable Integer id) throws SQLException {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserAuthentication userAuth = UserAuthentication.select(UserAuthentication.class, "SELECT * FROM user_authentication WHERE username=#1#", username);
        auctionRestTemplate.post(auctionURL + "/" + userAuth.getPlayerId() + "/buy/" + id, null, String.class);
        return new RedirectView("/player/trading");
    }

    /**
     * Retrieves information about the player's available resources and builds a model and view for creating a new auction
     * @return the model and view for creating a new auction
     * @throws SQLException
     */
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

    /**
     * Creates a new auction, provided the player has enough of the offered resource.
     * @param form backing object containing information about the new auction
     * @param result result of the validation of the backing object
     * @return If the backing object passes validation, the a redirection to the main auction page is returned,
     *          otherwise, the model and view of the auction creation page is returned with the values of the
     *          backing object added.
     * @throws SQLException
     */
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
        auctionRestTemplate.post(auctionURL + "/new", wrapper, String.class);
        return new RedirectView("/player/trading");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        gameContentRestTemplate = new DigestRestTemplate(gamecontentURL, serverUsername, serverHashedPassword);
        playerResourcesRestTemplate = new DigestRestTemplate(playerResourcesURL, serverUsername, serverHashedPassword);
        auctionRestTemplate = new DigestRestTemplate(auctionURL, serverUsername, serverHashedPassword);
    }
}
