package com.payby.gateway.openapi.response;

import java.io.Serializable;

import com.payby.gateway.openapi.model.Payer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetCashierUrlInfoResult implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8275958046615417311L;
    /**
     *
     */
    private Payer payer;

}