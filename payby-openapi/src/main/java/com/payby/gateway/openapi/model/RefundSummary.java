package com.payby.gateway.openapi.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class RefundSummary implements Serializable{

    private static final long serialVersionUID = 129447L;

    private ExternalMoney acquireAmount;

    private ExternalMoney remainRefundAmount;
    
    private List<SharingRemainRefundInfo> sharingRemainRefundInfoList;

}
