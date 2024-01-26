package com.payby.gateway.openapi.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import lombok.Data;

@Data
public class TransferToBankOrder implements Serializable {
    private static final long serialVersionUID = 8317786269111904442L;

    private String merchantOrderNo;

    private String orderNo;

    private String status;

    private String holderName;

    private String iban;

    private String swiftCode;

    private ExternalMoney amount;

    private String notifyUrl;

    private String memo;

    private Date requestTime;

    private String product;

    private TransferToBankPaymentInfo paymentInfo;

    private String failCode;

    private String failDes;

    private String beneficiaryAddress;

    private ExternalMoney fundoutAmount;

    private BigDecimal rate;

    private String networkCode;

    private String bankReference;
}
