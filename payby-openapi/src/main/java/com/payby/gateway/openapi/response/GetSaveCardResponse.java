package com.payby.gateway.openapi.response;

import java.io.Serializable;

import com.payby.gateway.openapi.model.CardInfo;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GetSaveCardResponse implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -4864442952261660174L;
    private CardInfo cardInfo;

}