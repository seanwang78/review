package com.payby.gateway.openapi.request;

import java.io.Serializable;

import lombok.Data;

@Data
public class CardIndexRequest implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1127545963764816588L;
    private String cardToken;

}