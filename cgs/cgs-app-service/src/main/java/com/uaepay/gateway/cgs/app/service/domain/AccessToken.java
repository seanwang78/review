package com.uaepay.gateway.cgs.app.service.domain;

import java.io.Serializable;

import com.uaepay.gateway.cgs.app.facade.domain.AccessMember;

import lombok.Data;

@Data
public class AccessToken implements Serializable {

    private static final long serialVersionUID = 1L;

    public AccessToken() {}

    public AccessToken(AccessMember accessMember, String accessToken, String accessKey) {
        this.accessMember = accessMember;
        this.accessToken = accessToken;
        this.accessKey = accessKey;
    }

    private AccessMember accessMember;

    private String accessToken;

    private String accessKey;

}
