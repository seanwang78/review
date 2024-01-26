package com.payby.gateway.openapi.request;

import java.io.Serializable;

import com.payby.gateway.openapi.model.ExternalMoney;

import lombok.Data;


@Data
public class PlaceTransferOrderRequest implements Serializable {
    private static final long serialVersionUID = 1591718822210545190L;

    private ExternalMoney amount;

    private String merchantOrderNo;

    private String notifyUrl;
    
    private String memo;
    
    private String beneficiaryIdentityType;
    
    
    private String beneficiaryIdentity;
    
    private String beneficiaryFullName;
    

}