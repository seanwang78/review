package com.payby.gateway.openapi.model.crypto;

import java.io.Serializable;

import com.payby.gateway.openapi.model.ExternalMoney;

import lombok.Data;

@Data
public class RefundOrder implements Serializable {
    private static final long serialVersionUID = 8317786269111904442L;

    private String refundMerchantOrderNo;

    private String orderNo;

    private String originMerchantOrderNo;

    private String status;

    private ExternalMoney amount;

    private String secondaryMerchantId;

    private String deviceId;

    private String notifyUrl;

    private String reason;

    private String failCode;

    private String failDes;

    private String reserved;
}
