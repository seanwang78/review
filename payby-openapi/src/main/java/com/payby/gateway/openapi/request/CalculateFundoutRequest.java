package com.payby.gateway.openapi.request;

import java.io.Serializable;

import com.payby.gateway.openapi.model.ExternalMoney;

import lombok.Data;

@Data
public class CalculateFundoutRequest implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5826837120931021747L;

    private ExternalMoney orderAmount;

    private String networkCode;

    private String fundoutCurrencyCode;

}