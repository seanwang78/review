package com.payby.gateway.openapi.request;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;


@Data
@Accessors(chain = true)
@Builder
public class AuthTokenRequest implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = -4769774233016346541L;
    /** uid */
    private String identity;
    /** mobile */
    private String mobile;
    /** deviceId */
    private String deviceId;
}
