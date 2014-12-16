package com.distributed.springtest.utils.wrappers;

/**
 * Created by Patrik on 2014-12-16.
 */
public class AuctionWrapper {
    private Integer id;
    private Integer offerResourceId;
    private Integer offerAmount;
    private Integer demandResourceId;
    private Integer demandAmount;

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
