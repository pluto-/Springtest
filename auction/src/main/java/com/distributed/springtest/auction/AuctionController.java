package com.distributed.springtest.auction;

import com.distributed.springtest.auction.records.Auction;
import com.distributed.springtest.auction.records.CompletedAuction;
import com.distributed.springtest.utils.records.gamecontent.ResourceInfo;
import com.distributed.springtest.utils.records.playerresources.Resource;
import com.distributed.springtest.utils.wrappers.AuctionWrapper;
import com.distributed.springtest.utils.wrappers.PlayerResourceModificationWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.sql.SQLException;
import java.util.*;

/**
 * Created by Patrik on 2014-12-16.
 */
@RestController
public class AuctionController {

    private static final Logger logger = LoggerFactory.getLogger(AuctionController.class);

    private static String playerResourcesURL = "http://213.21.115.53:8080";
    private static String gamecontentURL = "http://94.254.18.40:8080";

    @RequestMapping("/new")
    public ResponseEntity<String> newAuction(@RequestBody AuctionWrapper incomingAuction) {
        logger.info("new auction");
        PlayerResourceModificationWrapper wrapper = new PlayerResourceModificationWrapper();
        wrapper.setPlayerId(incomingAuction.getSellerId());
        wrapper.setResourceId(incomingAuction.getOfferResourceId());
        wrapper.setResourceAmount(-1 * incomingAuction.getOfferAmount());
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(playerResourcesURL + "/resources/modify", wrapper);
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
            restTemplate.put(playerResourcesURL + "/resources/modify", wrapper);
        }
        return new ResponseEntity<String>("Ok.", HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<AuctionWrapper>> getAuctions() throws SQLException {
        List<Auction> auctions = Auction.selectAll(Auction.class, "SELECT * FROM auctions WHERE enabled = true AND completed = false");
        RestTemplate restTemplate = new RestTemplate();
        List<ResourceInfo> resources = Arrays.asList(restTemplate.getForObject(gamecontentURL + "/resources", ResourceInfo[].class));
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

    @RequestMapping("{playerId}/buy/{auctionId}")
    public ResponseEntity<String> buy(@PathVariable Integer playerId, @PathVariable Integer auctionId) throws SQLException {
        Auction auction = Auction.findById(Auction.class, auctionId);
        PlayerResourceModificationWrapper wrapper = new PlayerResourceModificationWrapper();
        wrapper.setPlayerId(playerId);
        wrapper.setResourceId(auction.getDemandResourceId());
        wrapper.setResourceAmount(-1 * auction.getDemandAmount());
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.put(playerResourcesURL + "/resources/modify", wrapper);
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
            restTemplate.put(playerResourcesURL + "/resources/modify", wrapper);
        }
        return new ResponseEntity<String>("Ok.", HttpStatus.OK);
    }
}