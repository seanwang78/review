package com.payby.gateway.openapi.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class AccessoryContent implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 4049270716362576896L;

    private AmountDetail amountDetail = new AmountDetail();

    private GoodsDetail goodsDetail = new GoodsDetail();

    private TerminalDetail terminalDetail = new TerminalDetail();
}
