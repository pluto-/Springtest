package com.distributed.springtest.utils.wrappers;

/**
 * Created by Jonas on 2014-12-10.
 */
public class BuyBuildingWrapper {

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setBuildingId(int buildingId) {
        this.buildingId = buildingId;
    }

    public BuyBuildingWrapper() {

    }

    private int playerId;
    private int buildingId;

    public int getPlayerId() {
        return playerId;
    }

    public int getBuildingId() {
        return buildingId;
    }

    public BuyBuildingWrapper(int playerId, int buildingId) {
        this.playerId = playerId;
        this.buildingId = buildingId;
    }
}
