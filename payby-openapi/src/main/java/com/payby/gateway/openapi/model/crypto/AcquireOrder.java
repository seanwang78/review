package com.payby.gateway.openapi.model.crypto;

import java.io.Serializable;
import java.util.Date;

import com.payby.gateway.openapi.model.ExternalMoney;

import lombok.Data;

@Data
public class AcquireOrder implements Serializable {
    private static final long serialVersionUID = 8317786269111904442L;

    private String merchantOrderNo;

    private String orderNo;

    private String status;

    private PaymentInfo paymentInfo;

    private String product;

    private ExternalMoney totalAmount;

    private String payeeMid;

    private Date expiredTime;

    private String notifyUrl;

    private String subject;

    private Date requestTime;

    private AccessoryContent accessoryContent;

    private String paySceneCode;

    private String deviceId;

    private String secondaryMerchantId;

    private String failCode;

    private String failDes;

    private String revoked;

    private String reserved;

}
