package com.payby.gateway.openapi.response;

import java.io.Serializable;

import lombok.Data;

@Data
public class AuthTokenResponse implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -1252544797147351046L;
    private String authToken;
}
