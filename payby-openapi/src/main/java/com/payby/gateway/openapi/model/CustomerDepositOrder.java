package com.payby.gateway.openapi.model;

import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class CustomerDepositOrder implements Serializable {

    private static final long serialVersionUID = 8898495398456897367L;

    private String customerId;

    private String status;

    private String product;

    private String orderNo;

    private ExternalMoney depositAmount;

    private ExternalMoney settledAmount;

    private ExternalMoney fee;

    private String network;

    private String txHash;

    private Date confirmedTime;

}
