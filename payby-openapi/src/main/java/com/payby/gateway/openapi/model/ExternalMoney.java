package com.payby.gateway.openapi.model;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExternalMoney implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -416974060079942851L;

    private BigDecimal amount;

    private String currency;

}
