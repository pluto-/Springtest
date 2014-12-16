package com.distributed.springtest.auction.records;

import com.jajja.jorm.Jorm;
import com.jajja.jorm.Record;
import java.sql.Timestamp;

@Jorm(database="auction", schema="public", table="auctions", primaryKey="id")
public class Auction extends Record {
    public Integer getId() {
        return get("id", Integer.class);
    }

    public void setId(Integer id) {
        set("id", id);
    }

    public Integer getSellerId() {
        return get("seller_id", Integer.class);
    }

    public void setSellerId(Integer sellerId) {
        set("seller_id", sellerId);
    }

    public Timestamp getCreatedAt() {
        return get("created_at", Timestamp.class);
    }

    public void setCreatedAt(Timestamp createdAt) {
        set("created_at", createdAt);
    }

    public Boolean getCompleted() {
        return get("completed", Boolean.class);
    }

    public void setCompleted(Boolean completed) {
        set("completed", completed);
    }

    public Boolean getEnabled() {
        return get("enabled", Boolean.class);
    }

    public void setEnabled(Boolean enabled) {
        set("enabled", enabled);
    }

    public Integer getBuyerId() {
        return get("buyer_id", Integer.class);
    }

    public void setBuyerId(Integer buyerId) {
        set("buyer_id", buyerId);
    }

    public Integer getDemandResourceId() {
        return get("demand_resource_id", Integer.class);
    }

    public void setDemandResourceId(Integer demandResourceId) {
        set("demand_resource_id", demandResourceId);
    }

    public Integer getDemandAmount() {
        return get("demand_amount", Integer.class);
    }

    public void setDemandAmount(Integer demandAmount) {
        set("demand_amount", demandAmount);
    }

    public Integer getOfferResourceId() {
        return get("offer_resource_id", Integer.class);
    }

    public void setOfferResourceId(Integer offerResourceId) {
        set("offer_resource_id", offerResourceId);
    }

    public Integer getOfferAmount() {
        return get("offer_amount", Integer.class);
    }

    public void setOfferAmount(Integer offerAmount) {
        set("offer_amount", offerAmount);
    }
}