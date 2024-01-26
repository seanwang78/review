package com.payby.gateway.openapi.response;

import java.io.Serializable;

import com.payby.gateway.openapi.model.RefundSummary;
import com.payby.gateway.openapi.model.crypto.RefundOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PlaceCryptoRefundOrderResponse implements Serializable {
    private static final long serialVersionUID = 8317786269111904442L;

    private RefundOrder refundOrder;

    private RefundSummary refundSummary;

}
