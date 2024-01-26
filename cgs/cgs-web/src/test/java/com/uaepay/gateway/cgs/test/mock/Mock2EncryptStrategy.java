package com.uaepay.gateway.cgs.test.mock;

import com.uaepay.gateway.cgs.domainservice.encrypt.EncryptStrategy;
import com.uaepay.gateway.cgs.domainservice.encrypt.parameter.ContextParameter;
import org.springframework.stereotype.Service;

@Service
public class Mock2EncryptStrategy implements EncryptStrategy {

    @Override
    public String getServiceCode() {
        return "MOCK2";
    }

    @Override
    public String convert(String fieldName, String original, ContextParameter parameter) {
        return null;
    }
}
