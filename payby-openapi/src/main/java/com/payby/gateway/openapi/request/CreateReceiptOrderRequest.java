package com.payby.gateway.openapi.request;

import java.io.Serializable;

import com.payby.gateway.openapi.model.Receipt;

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
public class CreateReceiptOrderRequest implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7500021341698358309L;

    private String receiverEmail;

    private String memberId;

    private String receiverMobileNumber;

    private Receipt receipt;

}
