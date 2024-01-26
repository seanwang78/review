package com.payby.gateway.openapi.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SharingRemainRefundInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5893209124907549287L;

    private ExternalMoney remainRefundAmount;

    private String memeberId;

}
