package com.payby.gateway.openapi.response;


import java.io.Serializable;

import com.payby.gateway.openapi.model.TransferOrder;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetTransferOrderResponse implements Serializable {
    private static final long serialVersionUID = 4317629852561460030L;

    private TransferOrder transferOrder;
    
}