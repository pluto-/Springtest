package com.distributed.springtest.utils.wrappers;

import com.distributed.springtest.utils.records.playerresources.Building;
import com.distributed.springtest.utils.records.playerresources.Construction;
import com.distributed.springtest.utils.records.playerresources.Resource;

import java.util.List;

/**
 * Created by Jonas on 2014-12-09.
 */
public class PlayerStateWrapper {

    private List<Resource> resources;
    private List<Building> buildings;
    private List<Construction> constructions;

    public PlayerStateWrapper(List<Resource> resources, List<Building> buildings, List<Construction> constructions) {
        this.resources = resources;
        this.buildings = buildings;
        this.constructions = constructions;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public List<Building> getBuildings() {
        return buildings;
    }

    public List<Construction> getConstructions() {
        return constructions;
    }
}
