package com.payby.gateway.openapi.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class PaymentInfo implements Serializable {
    private static final long serialVersionUID = 8317786269111904442L;

    private ExternalMoney paidAmount;

    private Date paidTime;

    private String payerMid;

    private ExternalMoney payeeFeeAmount;

    private ExternalMoney payerFeeAmount;

    private String payChannel;

    private ExternalMoney settlementAmount;

    private CardInfo cardInfo;

}
