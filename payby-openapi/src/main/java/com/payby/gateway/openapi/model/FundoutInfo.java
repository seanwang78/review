package com.payby.gateway.openapi.model;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

@Data
public class FundoutInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -152864993112155363L;

    private String networkCode;

    private BigDecimal rate;

    private ExternalMoney orderAmount;

    private ExternalMoney feeAmount;

    private ExternalMoney fundoutAmount;

}