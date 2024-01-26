package com.payby.gateway.openapi.response;

import java.io.Serializable;

import com.payby.gateway.openapi.model.TransferToBankOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlaceTransferToBankOrderResponse implements Serializable {
    private static final long serialVersionUID = 1591718822210545190L;

    private TransferToBankOrder transferToBankOrder;
}