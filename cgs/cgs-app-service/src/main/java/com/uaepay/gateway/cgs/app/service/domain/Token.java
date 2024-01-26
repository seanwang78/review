package com.uaepay.gateway.cgs.app.service.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    private String iss;
    private String tk;
    private Long exp;
    private int refreshThreshold;
}