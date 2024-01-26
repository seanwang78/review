package com.payby.gateway.openapi.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class Payer implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 599285053736146064L;

    private String mobileNumberMask;

}
