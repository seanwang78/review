package com.payby.gateway.openapi.request;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.payby.gateway.openapi.model.ExternalMoney;
import com.payby.gateway.openapi.model.crypto.AccessoryContent;

import lombok.Data;

@Data
public class PlaceCryptoOrderRequest implements Serializable {
    private static final long serialVersionUID = 1591718822210545190L;

    private ExternalMoney totalAmount;

    private String merchantOrderNo;

    private String notifyUrl;

    private String payeeMid;

    private String subject;

    private String paySceneCode;

    private String deviceId;

    private String reserved;

    private Date expiredTime;

    private Map<String, String> paySceneParams = new HashMap<String, String>();

    private AccessoryContent accessoryContent = new AccessoryContent();
}