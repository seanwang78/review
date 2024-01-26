package com.payby.gateway.openapi.model;

import lombok.Data;

@Data
public class InappSignContent {

    private String iapAppId;

    private String iapDeviceId;

    private String iapPartnerId;

    private String token;


}
