package com.payby.gateway.openapi.response;

import java.io.Serializable;

import com.payby.gateway.openapi.model.FundoutInfo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CalculateFundoutResponse implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6413787038837123303L;

    private FundoutInfo fundoutInfo;

}