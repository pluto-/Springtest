package com.distributed.springtest.auction;

import com.distributed.springtest.auction.records.Auction;
import com.distributed.springtest.auction.records.CompletedAuction;
import com.distributed.springtest.utils.records.gamecontent.ResourceInfo;
import com.distributed.springtest.utils.security.DigestHandler;
import com.distributed.springtest.utils.security.DigestRestTemplate;
import com.distributed.springtest.utils.wrappers.AuctionWrapper;
import com.distributed.springtest.utils.wrappers.PlayerResourceModificationWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

/**
 * Controller for the REST API for the auction subsystem.
 */
@RestController
public class AuctionController implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(AuctionController.class);

    protected static DigestHandler digestHandler;

    @Value("${hosts.playerresources}")
    private String playerResourcesURL;
    @Value("${hosts.gamecontent}")
    private String gamecontentURL;

    @Value("${subsystems.username}")
    private String subsystemUsername;
    @Value("${subsystems.password}")
    private String subsystemPassword;
    
    private static DigestRestTemplate playerResourcesRestTemplate;
    private static DigestRestTemplate gameContentRestTemplate;

    @Value("${digesthandler.path}")
    public void setDigestHandler(String filePath) {
        try {
            AuctionController.digestHandler = new DigestHandler(AuctionController.class.getResourceAsStream(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the current value of the request counter for a certain user
     * @param request
     * @return
     */
    @RequestMapping("/counter")
    public Object getPlayerResources(HttpServletRequest request) {
        int counter = digestHandler.getCounter(request.getHeader("username"));
        if(counter == -1) {
            return new ResponseEntity<Object>("Username does not exist.", HttpStatus.UNAUTHORIZED);
        }
        return counter;
    }

    /**
     * Creates a new auction, provided the player has enough of the offered resource
     * @param incomingAuction wrapper containing information about the auction to be created
     * @return OK on success, error code on failure
     */
    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public ResponseEntity<String> newAuction(@RequestBody AuctionWrapper incomingAuction) {
        logger.info("new auction");
        PlayerResourceModificationWrapper wrapper = new PlayerResourceModificationWrapper();
        wrapper.setPlayerId(incomingAuction.getSellerId());
        wrapper.setResourceId(incomingAuction.getOfferResourceId());
        wrapper.setResourceAmount((double)(-1 * incomingAuction.getOfferAmount()));
        try {
            playerResourcesRestTemplate.put(playerResourcesURL + "/resources/modify", wrapper);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<String>(e.getStatusText(), e.getStatusCode());
        }
        try {
            logger.info("starting auction creation");
            Auction auction = new Auction();
            auction.setSellerId(incomingAuction.getSellerId());
            auction.setOfferAmount(incomingAuction.getOfferAmount());
            auction.setOfferResourceId(incomingAuction.getOfferResourceId());
            auction.setDemandAmount(incomingAuction.getDemandAmount());
            auction.setDemandResourceId(incomingAuction.getDemandResourceId());
            auction.save();
            auction.transaction().commit();
            logger.info("auction created successfully");
        } catch (SQLException e) {
            logger.error("auction creation failed");
            //TODO Discuss in report about what happens if playerresources crashes here
            wrapper.setResourceAmount(-1 * wrapper.getResourceAmount());
            playerResourcesRestTemplate.put(playerResourcesURL + "/resources/modify", wrapper);
            return new ResponseEntity<String>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<String>("Ok.", HttpStatus.OK);
    }

    /**
     * Retrieves all active auctions in the system
     * @return list containing information about all active auctions in the system
     * @throws SQLException
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<AuctionWrapper>> getAuctions() throws SQLException {
        List<Auction> auctions = Auction.selectAll(Auction.class, "SELECT * FROM auctions WHERE enabled = true AND completed = false");
        List<ResourceInfo> resources = Arrays.asList(gameContentRestTemplate.get(gamecontentURL + "/resources", ResourceInfo[].class).getBody());
        Map<Integer, String> resourceNames = new HashMap<>();
        for(ResourceInfo resource : resources) {
            resourceNames.put(resource.getId(), resource.getName());
        }
        List<AuctionWrapper> resultList = new ArrayList<>();
        for(Auction auction : auctions) {
            AuctionWrapper wrapper = new AuctionWrapper();
            wrapper.setId(auction.getId());
            wrapper.setOfferResourceId(auction.getOfferResourceId());
            wrapper.setOfferAmount(auction.getOfferAmount());
            wrapper.setDemandResourceId(auction.getDemandResourceId());
            wrapper.setDemandAmount(auction.getDemandAmount());
            wrapper.setOfferResourceName(resourceNames.get(auction.getOfferResourceId()));
            wrapper.setDemandResourceName(resourceNames.get(auction.getDemandResourceId()));
            wrapper.setCreatedAt(auction.getCreatedAt());
            resultList.add(wrapper);
        }
        return new ResponseEntity<List<AuctionWrapper>>(resultList, HttpStatus.OK);
    }

    /**
     * Buys an auction for a player, provided the player has enough of the requested resource.
     * @param playerId player id of the buyer
     * @param auctionId auction id of the auction to be purchased
     * @return OK on success, error code on failure
     * @throws SQLException
     */
    @RequestMapping(value = "{playerId}/buy/{auctionId}", method = RequestMethod.POST)
    public ResponseEntity<String> buy(@PathVariable Integer playerId, @PathVariable Integer auctionId) throws SQLException {
        Auction auction = Auction.findById(Auction.class, auctionId);
        PlayerResourceModificationWrapper wrapper = new PlayerResourceModificationWrapper();
        wrapper.setPlayerId(playerId);
        wrapper.setResourceId(auction.getDemandResourceId());
        wrapper.setResourceAmount((double)(-1 * auction.getDemandAmount()));
        try {
            playerResourcesRestTemplate.put(playerResourcesURL + "/resources/modify", wrapper);
        } catch (HttpClientErrorException e) {
            return new ResponseEntity<String>(e.getStatusText(), e.getStatusCode());
        }
        try {
            auction.setBuyerId(playerId);
            auction.setEnabled(false);
            auction.setCompleted(true);
            auction.save();
            CompletedAuction completedAuction = new CompletedAuction();
            completedAuction.setAuctionId(auction.getId());
            completedAuction.save();
            completedAuction.transaction().commit();
        } catch (SQLException e) {
            wrapper.setResourceAmount(-1 * wrapper.getResourceAmount());
            playerResourcesRestTemplate.put(playerResourcesURL + "/resources/modify", wrapper);
        }
        return new ResponseEntity<String>("Ok.", HttpStatus.OK);
    }

    /**
     * Method called after properties have been set by Spring
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        playerResourcesRestTemplate = new DigestRestTemplate(playerResourcesURL, subsystemUsername, subsystemPassword);
        gameContentRestTemplate = new DigestRestTemplate(gamecontentURL, subsystemUsername, subsystemPassword);
    }
}