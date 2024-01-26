package com.payby.gateway.openapi.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class SharingInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5893209124907549287L;

    private ExternalMoney sharingAmount;

    private ExternalMoney sharingSettledAmount;
    
    private String sharingMid;

    private Integer sharingIdentitySeqId;

    private String sharingMemo;

}
