package com.distributed.springtest.utils.wrappers;

/**
 * Created by Patrik on 2014-12-16.
 */
public class PlayerResourceModificationWrapper {

    private Integer playerId;
    private Integer resourceId;
    private Double resourceAmount;

    public Double getResourceAmount() {
        return resourceAmount;
    }

    public void setResourceAmount(Double resourceAmount) {
        this.resourceAmount = resourceAmount;
    }

    public Integer getResourceId() {
        return resourceId;
    }

    public void setResourceId(Integer resourceId) {
        this.resourceId = resourceId;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }
}
