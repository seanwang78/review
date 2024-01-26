package com.payby.gateway.openapi.model;

import java.io.Serializable;
import java.math.BigDecimal;

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
public class Goods implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 4077596455165368679L;
    private ExternalMoney amount;
    private String id;
    private BigDecimal quantity;
    private String name1;
    private String name2;

}
