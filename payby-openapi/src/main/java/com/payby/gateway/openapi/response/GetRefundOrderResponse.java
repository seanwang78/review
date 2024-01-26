package com.payby.gateway.openapi.response;

import java.io.Serializable;

import com.payby.gateway.openapi.model.RefundOrder;
import com.payby.gateway.openapi.model.RefundSummary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetRefundOrderResponse implements Serializable {
  private static final long serialVersionUID = 831778904442L;



  private RefundOrder refundOrder;

  private RefundSummary refundSummary;


}
