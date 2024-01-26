package com.payby.gateway.openapi.request;

import java.io.Serializable;

import com.payby.gateway.openapi.model.ExternalMoney;

import lombok.Data;

@Data
public class PlaceCryptoRefundOrderRequest implements Serializable {
    private static final long serialVersionUID = 8317786269111904442L;

    private String refundMerchantOrderNo;

    private String originMerchantOrderNo;

    private String originOrderNo;

    private ExternalMoney amount;

    private String notifyUrl;

    private String operatorName;

    private String reason;

    private String deviceId;

    private String reserved;

}
