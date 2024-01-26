package com.payby.gateway.openapi.response;

import java.io.Serializable;

import com.payby.gateway.openapi.model.crypto.AcquireOrder;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CryptoOrderResponse implements Serializable {
    private static final long serialVersionUID = 159171890L;

    private AcquireOrder acquireOrder;

}