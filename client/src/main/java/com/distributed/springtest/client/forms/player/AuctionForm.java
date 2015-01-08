package com.distributed.springtest.client.forms.player;

import javax.validation.constraints.NotNull;

/**
 * Created by Patrik on 2014-12-16.
 */
public class AuctionForm {

    @NotNull
    private Integer offeredResourceId;
    @NotNull
    private Integer offeredAmount;
    @NotNull
    private Integer demandedResourceId;
    @NotNull
    private Integer demandedAmount;

    public Integer getOfferedResourceId() {
        return offeredResourceId;
    }

    public void setOfferedResourceId(Integer offeredResourceId) {
        this.offeredResourceId = offeredResourceId;
    }

    public Integer getOfferedAmount() {
        return offeredAmount;
    }

    public void setOfferedAmount(Integer offeredAmount) {
        this.offeredAmount = offeredAmount;
    }

    public Integer getDemandedResourceId() {
        return demandedResourceId;
    }

    public void setDemandedResourceId(Integer demandedResourceId) {
        this.demandedResourceId = demandedResourceId;
    }

    public Integer getDemandedAmount() {
        return demandedAmount;
    }

    public void setDemandedAmount(Integer demandedAmount) {
        this.demandedAmount = demandedAmount;
    }
}
