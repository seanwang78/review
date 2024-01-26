package com.payby.gateway.openapi.model.crypto;

import java.io.Serializable;

import com.payby.gateway.openapi.model.ExternalMoney;

import lombok.Data;

@Data
public class AmountDetail implements Serializable {
    private static final long serialVersionUID = -436642916824069447L;

    private ExternalMoney discountableAmount;

    private ExternalMoney amount;

    private ExternalMoney vatAmount;

    private ExternalMoney tipAmount;

    @Override
    public String toString() {
        return "AmountDetail.AmountDetailBuilder(discountableAmount=" + this.discountableAmount + ", netAmount="
            + this.amount + ", vatAmount=" + this.vatAmount + ", tipAmount=" + this.tipAmount + ")";
    }
}
