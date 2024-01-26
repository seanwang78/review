package com.payby.gateway.openapi.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class RefundOrder implements Serializable {
    private static final long serialVersionUID = 8317786269111904442L;

    private String refundMerchantOrderNo;

    private String orderNo;

    private String originMerchantOrderNo;

    private String status;

    private ExternalMoney amount;
    
    private ExternalMoney feeRefunded;

    private String secondaryMerchantId;

    private String deviceId;

    private String notifyUrl;

    private String reason;

    private String failCode;

    private String failDes;

    private String reserved;
    
    private Boolean refundSharingAmount;
    
    private List<SharingInfo> sharingInfoList;

}
