package com.payby.gateway.openapi.request;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class GetAddressRequest implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -756337267701053995L;

    private String customerId;

    private String assetCode;

    private String network;

}
