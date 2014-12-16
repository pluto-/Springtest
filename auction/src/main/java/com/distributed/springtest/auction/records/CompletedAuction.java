package com.distributed.springtest.auction.records;

import com.jajja.jorm.Jorm;
import com.jajja.jorm.Record;

@Jorm(database="auction", schema="public", table="completed_auctions", primaryKey="id")
public class CompletedAuction extends Record {
    public Integer getId() {
        return get("id", Integer.class);
    }

    public void setId(Integer id) {
        set("id", id);
    }

    public Integer getAuctionId() {
        return get("auction_id", Integer.class);
    }

    public void setAuctionId(Integer auctionId) {
        set("auction_id", auctionId);
    }

    public Boolean getProcessed() {
        return get("processed", Boolean.class);
    }

    public void setProcessed(Boolean processed) {
        set("processed", processed);
    }
}