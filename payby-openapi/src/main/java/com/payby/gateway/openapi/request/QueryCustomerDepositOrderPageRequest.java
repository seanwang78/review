package com.payby.gateway.openapi.request;

import com.payby.gateway.openapi.common.QueryOrderPageRequest;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@NoArgsConstructor
public class QueryCustomerDepositOrderPageRequest extends QueryOrderPageRequest {
    /**
     *
     */
    private static final long serialVersionUID = -1591338766667890928L;

    private String customerId;

}
