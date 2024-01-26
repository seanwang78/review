package com.payby.gateway.openapi.request;

import java.io.Serializable;

import lombok.Data;

@Data
public class GetStatementRequest implements Serializable {
    private static final long serialVersionUID = 1591718822210545190L;

    private String statementDate;

}