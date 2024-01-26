package com.payby.gateway.openapi.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class TransferOrder implements Serializable {
    private static final long serialVersionUID = 8317786269111904442L;

    private String merchantOrderNo;

    private String orderNo;

    private String status;

    private String beneficiaryIdentity;

    private String beneficiaryIdentityType;

    private String beneficiaryFullName;

    private ExternalMoney amount;

    private String notifyUrl;

    private String memo;

    private Date requestTime;

    private String product;

    private TransferPaymentInfo paymentInfo;

    private String failCode;

    private String failDes;

}
