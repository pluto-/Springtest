package com.distributed.springtest.client.forms.player;

import javax.validation.constraints.NotNull;

/**
 * Created by Jonas on 2014-12-11.
 */
public class ResourceForm {


    @NotNull
    private String name;

    @NotNull
    private String amount;

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}