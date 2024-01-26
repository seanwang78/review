package com.payby.gateway.openapi.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class TransferToBankPaymentInfo implements Serializable {
    private static final long serialVersionUID = 8317786269111904442L;

    private ExternalMoney payerFeeAmount;

    private Date arrivalTime;

}
