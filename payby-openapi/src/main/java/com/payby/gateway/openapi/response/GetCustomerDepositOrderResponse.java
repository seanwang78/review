package com.payby.gateway.openapi.response;

import java.io.Serializable;

import com.payby.gateway.openapi.model.CustomerDepositOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class GetCustomerDepositOrderResponse implements Serializable {
    private static final long serialVersionUID = 4317629852561460030L;

    private CustomerDepositOrder customerDepositOrder;

}