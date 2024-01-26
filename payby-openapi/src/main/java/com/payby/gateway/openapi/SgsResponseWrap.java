package com.payby.gateway.openapi;

import java.io.Serializable;

import lombok.Data;

@Data
public class SgsResponseWrap<T> implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5867791913050541148L;

    SgsHead head;

    T body;
}
