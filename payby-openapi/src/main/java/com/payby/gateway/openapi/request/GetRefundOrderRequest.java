package com.payby.gateway.openapi.request;

import java.io.Serializable;

import lombok.Data;

/**
 * 退款订单查询-请求
 */
@Data
public class GetRefundOrderRequest implements Serializable {
    private static final long serialVersionUID = 1591718822210545190L;

    /**
     * 退款商户订单号
     */
    private String refundMerchantOrderNo;
    
    private String orderNo;


}