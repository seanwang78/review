package com.payby.gateway.openapi.request;

import java.io.Serializable;

import lombok.Data;

@Data
public class ReceiptOrderIndexRequest implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -8372564728208207021L;
    private String receiptNo;
    private String orderNo;

}