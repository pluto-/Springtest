package com.distributed.springtest.auction;

/**
 * Created by Patrik on 2014-12-05.
 */

import com.distributed.springtest.auction.records.Auction;
import com.distributed.springtest.auction.records.CompletedAuction;
import com.distributed.springtest.utils.security.DigestRestTemplate;
import com.distributed.springtest.utils.wrappers.PlayerResourceModificationWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.client.HttpClientErrorException;

import java.sql.SQLException;
import java.util.List;

@ComponentScan
@EnableAutoConfiguration
public class Application implements InitializingBean {

    private static Logger logger = LoggerFactory.getLogger(Application.class);

    private static String playerResourcesURL;
    private static DigestRestTemplate restTemplate;

    @Value("${subsystems.username}")
    private String subsystemUsername;
    @Value("${subsystems.password}")
    private String subsystemPassword;

    @Value("${hosts.playerresources}")
    public void setPlayerResourcesURL(String playerResourcesURL) {
        Application.playerResourcesURL = playerResourcesURL;
    }

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
        processAuctions();
    }

    public static void processAuctions() {
        while(true) {
            try {
                List<CompletedAuction> auctions = CompletedAuction.selectAll(CompletedAuction.class, "SELECT * FROM completed_auctions WHERE processed = false");
                logger.info("Processing " + auctions.size() + " completed auctions");
                for(CompletedAuction completedAuction : auctions) {
                    Auction auction = Auction.findById(Auction.class, completedAuction.getId());
                    try {
                        PlayerResourceModificationWrapper wrapper = new PlayerResourceModificationWrapper();
                        wrapper.setResourceAmount((double)auction.getOfferAmount());
                        wrapper.setResourceId(auction.getOfferResourceId());
                        wrapper.setPlayerId(auction.getCompleted() ? auction.getBuyerId() : auction.getSellerId());
                        completedAuction.setProcessed(true);
                        completedAuction.save();
                        restTemplate.put(playerResourcesURL + "/resources/modify", wrapper);
                        completedAuction.transaction().commit();
                        logger.info("Processed auction id:" + auction.getId());
                    } catch (SQLException | HttpClientErrorException e) {
                        logger.error("Failed processing auction id:" + auction.getId());
                        e.printStackTrace();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                logger.info("Sleeping for 60 seconds");
                Thread.sleep(60000);
            } catch (InterruptedException e) {
            }
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        restTemplate = new DigestRestTemplate(playerResourcesURL, subsystemUsername, subsystemPassword);
    }
}