package com.distributed.springtest.utils.wrappers;

import java.sql.Timestamp;

/**
 * Created by Patrik on 2014-12-16.
 */
public class AuctionWrapper {
    private Timestamp createdAt;
    private Integer id;
    private Integer offerResourceId;
    private Integer offerAmount;
    private String offerResourceName;
    private Integer demandResourceId;
    private Integer demandAmount;
    private String demandResourceName;

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public String getDemandResourceName() {
        return demandResourceName;
    }

    public void setDemandResourceName(String demandResourceName) {
        this.demandResourceName = demandResourceName;
    }

    public String getOfferResourceName() {
        return offerResourceName;
    }

    public void setOfferResourceName(String offerResourceName) {
        this.offerResourceName = offerResourceName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    private Integer sellerId;

    public Integer getSellerId() {
        return sellerId;
    }

    public void setSellerId(Integer sellerId) {
        this.sellerId = sellerId;
    }

    public Integer getOfferResourceId() {
        return offerResourceId;
    }

    public void setOfferResourceId(Integer offerResourceId) {
        this.offerResourceId = offerResourceId;
    }

    public Integer getOfferAmount() {
        return offerAmount;
    }

    public void setOfferAmount(Integer offerAmount) {
        this.offerAmount = offerAmount;
    }

    public Integer getDemandResourceId() {
        return demandResourceId;
    }

    public void setDemandResourceId(Integer demandResourceId) {
        this.demandResourceId = demandResourceId;
    }

    public Integer getDemandAmount() {
        return demandAmount;
    }

    public void setDemandAmount(Integer demandAmount) {
        this.demandAmount = demandAmount;
    }
}
