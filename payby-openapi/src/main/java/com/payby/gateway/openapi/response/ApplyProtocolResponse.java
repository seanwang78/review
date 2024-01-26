package com.payby.gateway.openapi.response;

import java.io.Serializable;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ApplyProtocolResponse implements Serializable {
    private static final long serialVersionUID = 4317629852561460030L;

    private Map<String, String> interActionParams;

}