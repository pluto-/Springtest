package com.distributed.springtest.client.forms.player;

import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * Created by Patrik on 2014-12-09.
 */
public class ConstructionForm {

    @NotNull
    private String name;

    @NotNull
    private Timestamp started;

    @NotNull
    private Timestamp finishes;

    public Timestamp getFinishes() {
        return finishes;
    }

    public void setFinishes(Timestamp finishes) {
        this.finishes = finishes;
    }

    public Timestamp getStarted() {
        return started;
    }

    public void setStarted(Timestamp started) {
        this.started = started;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}