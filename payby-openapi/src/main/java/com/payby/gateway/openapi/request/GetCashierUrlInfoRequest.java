package com.payby.gateway.openapi.request;

import java.io.Serializable;

import lombok.Data;

@Data
public class GetCashierUrlInfoRequest implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -563741580748169348L;
    private String tokenUrl;

}