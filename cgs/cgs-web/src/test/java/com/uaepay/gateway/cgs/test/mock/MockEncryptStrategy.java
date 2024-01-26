package com.uaepay.gateway.cgs.test.mock;

import com.uaepay.gateway.cgs.domainservice.encrypt.EncryptStrategy;
import com.uaepay.gateway.cgs.domainservice.encrypt.parameter.ContextParameter;
import org.springframework.stereotype.Service;

@Service
public class MockEncryptStrategy implements EncryptStrategy {

    @Override
    public String getServiceCode() {
        return "MOCK";
    }

    @Override
    public String convert(String fieldName, String original, ContextParameter parameter) {
        return null;
    }
}
