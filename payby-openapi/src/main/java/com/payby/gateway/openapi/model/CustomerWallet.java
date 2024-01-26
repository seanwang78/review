package com.payby.gateway.openapi.model;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CustomerWallet implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -756337267701053995L;

    private String customerId;

    private String status;

    private String product;

    private String assetCode;

    private String network;

    private String address;

    private BigDecimal minDeposit;

    private Integer confirm;

}
