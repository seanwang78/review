package com.payby.gateway.openapi.model;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ReceiptOrder implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2105715175200243402L;

    private String receiverEmail;

    private String memberId;

    private String receiverMobileNumber;

    private Receipt receipt;

    private String orderNo;

    private String partnerMid;

}
