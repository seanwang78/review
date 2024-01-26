package com.uaepay.gateway.cgs.domainservice.encrypt.strategy;

import org.springframework.stereotype.Service;

@Service
public class DefaultEncryptStrategy extends AbstractSaltEncryptStrategy {

    public static final String STRATEGY_CODE = "DEFAULT";

    @Override
    public String getServiceCode() {
        return STRATEGY_CODE;
    }

}
