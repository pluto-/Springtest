package com.distributed.springtest.utils.wrappers;

/**
 * Created by Patrik on 2014-12-16.
 */
public class PlayerResourceModificationWrapper {

    private Integer playerId;
    private Integer resourceId;
    private Integer resourceAmount;

    public Integer getResourceAmount() {
        return resourceAmount;
    }

    public void setResourceAmount(Integer resourceAmount) {
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
