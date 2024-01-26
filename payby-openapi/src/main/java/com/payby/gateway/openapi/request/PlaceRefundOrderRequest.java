package com.payby.gateway.openapi.request;

import java.io.Serializable;
import java.util.List;

import com.payby.gateway.openapi.model.ExternalMoney;
import com.payby.gateway.openapi.model.SharingInfo;
import com.payby.gateway.openapi.model.SharingParam;

import lombok.Data;

@Data
public class PlaceRefundOrderRequest implements Serializable {
    private static final long serialVersionUID = 8317786269111904442L;

    private String refundMerchantOrderNo;

    private String originMerchantOrderNo;

    private String originOrderNo;

    private ExternalMoney amount;

    private String notifyUrl;

    private String operatorName;

    private String reason;

    private String secondaryMerchantId;

    private String deviceId;

    private String reserved;

    private Boolean refundSharingAmount;

    private List<SharingParam> sharingParamList;

}
