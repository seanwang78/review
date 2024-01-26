package com.payby.gateway.openapi.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class FundoutAbility implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8445859809527837565L;

    private String name;

    private String beneficiaryType;

    private String networkCode;

    private String countryCode;

    private String fundoutCurrencyCode;

}
