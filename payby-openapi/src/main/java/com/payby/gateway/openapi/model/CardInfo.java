package com.payby.gateway.openapi.model;

import java.io.Serializable;
import java.util.Date;

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
public class CardInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -436642916824069447L;

    private String brand;

    private String cardToken;

    private String last4;

    private String cardType;

    private String expMonth;

    private String expYear;

    private Date cardTokenExpTime;
}
