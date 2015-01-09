package com.distributed.springtest.auction;

/**
 * Main class for the auction subsystem.
 * Starts the rest service and processes completed auctions.
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
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.sql.SQLException;
import java.util.List;

@ComponentScan
@EnableAutoConfiguration
@EnableWebMvc
@Configuration
public class Application extends WebMvcConfigurerAdapter implements InitializingBean {

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

    /**
     * Function for processing completed auctions.
     */
    public static void processAuctions() {
        while(true) {
            try {
                List<CompletedAuction> auctions = CompletedAuction.selectAll(CompletedAuction.class, "SELECT * FROM completed_auctions WHERE processed = false");
                logger.info("Processing " + auctions.size() + " completed auctions");
                for(CompletedAuction completedAuction : auctions) {
                    Auction auction = Auction.findById(Auction.class, completedAuction.getAuctionId());
                    boolean buyerCompleted = false, sellerCompleted = false;
                    PlayerResourceModificationWrapper wrapper = new PlayerResourceModificationWrapper();
                    try {
                        if(auction.getCompleted()) {
                            wrapper.setResourceAmount((double) auction.getDemandAmount());
                            wrapper.setResourceId(auction.getDemandResourceId());
                            wrapper.setPlayerId(auction.getSellerId());
                            restTemplate.put(playerResourcesURL + "/resources/modify", wrapper);
                            sellerCompleted = true;
                            wrapper.setResourceAmount((double) auction.getOfferAmount());
                            wrapper.setResourceId(auction.getOfferResourceId());
                            wrapper.setPlayerId(auction.getBuyerId());
                            restTemplate.put(playerResourcesURL + "/resources/modify", wrapper);
                            buyerCompleted = true;
                        } else {
                            wrapper.setResourceId(auction.getOfferResourceId());
                            wrapper.setResourceAmount((double) auction.getOfferAmount());
                            wrapper.setPlayerId(auction.getSellerId());
                            restTemplate.put(playerResourcesURL + "/resources/modify", wrapper);
                            sellerCompleted = true;
                        }
                        completedAuction.setProcessed(true);
                        completedAuction.save();
                        completedAuction.transaction().commit();
                        logger.info("Processed auction id:" + auction.getId());
                    } catch (SQLException e1) {
                        logger.error("Processing failed auction id:" + auction.getId() + " SQL Exception, reverting");
                        e1.printStackTrace();
                        if(buyerCompleted) {
                            wrapper.setPlayerId(auction.getBuyerId());
                            wrapper.setResourceAmount(auction.getOfferAmount() * -1.0);
                            wrapper.setResourceId(auction.getOfferResourceId());
                            restTemplate.put(playerResourcesURL + "/resources/modify", wrapper);
                        }
                        if(sellerCompleted) {
                            wrapper.setPlayerId(auction.getSellerId());
                            if(auction.getCompleted()) {
                                wrapper.setResourceAmount(auction.getDemandAmount() * -1.0);
                                wrapper.setResourceId(auction.getDemandResourceId());
                            } else {
                                wrapper.setResourceAmount(auction.getOfferAmount() * -1.0);
                                wrapper.setResourceId(auction.getOfferResourceId());
                            }
                            restTemplate.put(playerResourcesURL + "/resources/modify", wrapper);
                        }
                    } catch(HttpClientErrorException e) {
                        logger.error("Failed processing auction id:" + auction.getId());
                        e.printStackTrace();
                        if(auction.getCompleted() && sellerCompleted) {
                            Auction correctionAuction = new Auction();
                            correctionAuction.setCompleted(false);
                            correctionAuction.setEnabled(false);
                            correctionAuction.setOfferAmount(auction.getDemandAmount() * -1);
                            correctionAuction.setOfferResourceId(auction.getDemandResourceId());
                            correctionAuction.setSellerId(auction.getSellerId());
                            correctionAuction.save();
                            CompletedAuction correctionCompletedAuction = new CompletedAuction();
                            correctionCompletedAuction.setAuctionId(correctionAuction.getId());
                            correctionCompletedAuction.save();
                            correctionAuction.transaction().commit();
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                logger.info("Sleeping for 15 seconds");
                Thread.sleep(15000);
            } catch (InterruptedException e) {
            }
        }
    }

    /**
     * Method called after properties have been set by Spring
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        restTemplate = new DigestRestTemplate(playerResourcesURL, subsystemUsername, subsystemPassword);
    }

    /**
     * Method called by Spring to add an Interceptor for Digest handling.
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new Interceptor());
    }
}