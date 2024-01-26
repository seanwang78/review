package com.payby.gateway.openapi.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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
public class Receipt implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -3515975069191058272L;

    private String address;

    private String cashier;

    private String counter;

    private Integer count;

    private ExternalMoney changeAmount;

    private ExternalMoney payAmount;

    private ExternalMoney totalAmount;

    private ExternalMoney totalBeforeVat;

    private ExternalMoney vatAmount;

    private BigDecimal vatRate;

    private Date date;

    private String paymentChannel;

    private String trn;

    private String type;

    private List<String> paymentId;

    private String pos;

    private String email;

    private String notes;

    private String receiptNo;

    private String refundNo;

    private String store;

    private String tel;

    private String name;

    private List<Goods> goodsList;

}
