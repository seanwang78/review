package com.payby.gateway.openapi.response;


import java.io.Serializable;
import java.util.Map;

import com.payby.gateway.openapi.model.AcquireOrder;

import lombok.Data;

@Data
public class PlaceOrderResponse implements Serializable {
    private static final long serialVersionUID = 4317629852561460030L;

    private AcquireOrder acquireOrder;
    
    private Map<String,String> interActionParams;
}