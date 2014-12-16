package com.distributed.springtest.auction;

import com.distributed.springtest.auction.records.Auction;
import com.distributed.springtest.utils.wrappers.AuctionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patrik on 2014-12-16.
 */
@RestController
public class AuctionController {

    private static final Logger logger = LoggerFactory.getLogger(AuctionController.class);

    @Value("${hosts.playerresources}")
    private String playerResourcesURL;

    @RequestMapping("/new")
    public ResponseEntity<String> newAuction(@RequestBody AuctionWrapper incomingAuction) throws SQLException {
        Auction auction = new Auction();
        auction.setSellerId(incomingAuction.getSellerId());
        auction.setOfferAmount(incomingAuction.getOfferAmount());
        auction.setOfferResourceId(incomingAuction.getOfferResourceId());
        auction.setDemandAmount(incomingAuction.getDemandAmount());
        auction.setDemandResourceId(incomingAuction.getDemandResourceId());
        auction.save();
        auction.transaction().commit();
        return new ResponseEntity<String>("Ok.", HttpStatus.OK);
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<AuctionWrapper>> getAuctions() throws SQLException {
        List<Auction> auctions = Auction.selectAll(Auction.class, "SELECT * FROM auctions WHERE enabled = true");
        List<AuctionWrapper> resultList = new ArrayList<>();
        for(Auction auction : auctions) {
            AuctionWrapper wrapper = new AuctionWrapper();
            wrapper.setId(auction.getId());
            wrapper.setOfferResourceId(auction.getOfferResourceId());
            wrapper.setOfferAmount(auction.getOfferAmount());
            wrapper.setDemandResourceId(auction.getDemandResourceId());
            wrapper.setDemandAmount(auction.getDemandAmount());
            resultList.add(wrapper);
        }
        return new ResponseEntity<List<AuctionWrapper>>(resultList, HttpStatus.OK);
    }

    @RequestMapping("{playerId}/buy/{auctionId}")
    public ResponseEntity<String> buy(@PathVariable Integer playerId, @PathVariable Integer auctionId) {

        return new ResponseEntity<String>("Ok.", HttpStatus.OK);
    }
}