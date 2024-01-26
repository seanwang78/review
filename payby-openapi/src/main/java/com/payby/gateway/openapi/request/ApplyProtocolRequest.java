package com.payby.gateway.openapi.request;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class ApplyProtocolRequest implements Serializable {
    private static final long serialVersionUID = 1591718822210545190L;

    private String merchantOrderNo;

    private String notifyUrl;

    private String langType;

    private String signerMerchantId;

    private String accessType;

    private String protocolSceneCode;

    private Map<String, String> protocolSceneParams = new HashMap<String, String>();

    private Date expiredTime;

}