package com.uaepay.gateway.cgs.app.service.domain;

import lombok.Data;

@Data
public class AccessInfo {

    public AccessInfo(String accessToken, String accessKey) {
        this.accessToken = accessToken;
        this.accessKey = accessKey;
    }

    private String accessToken;

    private String accessKey;

}
