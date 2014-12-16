package com.distributed.springtest.utils.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Created by Jonas on 2014-12-15.
 */
@ResponseStatus(value= HttpStatus.NOT_ACCEPTABLE, reason="Not enough resources.")
public class NotEnoughResourcesException extends Exception {

    public NotEnoughResourcesException() {
        super("Not enough resources.");
    }
}
