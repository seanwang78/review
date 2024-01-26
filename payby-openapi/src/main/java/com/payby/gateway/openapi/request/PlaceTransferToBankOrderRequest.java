package com.payby.gateway.openapi.request;

import java.io.Serializable;

import com.payby.gateway.openapi.model.ExternalMoney;

import lombok.Data;

@Data
public class PlaceTransferToBankOrderRequest implements Serializable {
    private static final long serialVersionUID = 1591718822210545190L;

    private ExternalMoney amount;

    private String merchantOrderNo;

    private String notifyUrl;

    private String memo;

    private String holderName;

    private String iban;

    private String swiftCode;

    private String beneficiaryAddress;

    private String networkCode;

    private String accountNo;

    private String bankName;

    private String countryCode;

    private String fundoutCurrencyCode;

    private String fedwireCode;

    private String intermediaryBank;

    private String branchName;

    private String beneficiaryType;

}