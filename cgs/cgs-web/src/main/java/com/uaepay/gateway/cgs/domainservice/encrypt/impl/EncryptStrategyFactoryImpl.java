package com.uaepay.gateway.cgs.domainservice.encrypt.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.template.AbstractCodeServiceFactory;
import com.uaepay.gateway.cgs.domainservice.encrypt.EncryptStrategy;
import com.uaepay.gateway.cgs.domainservice.encrypt.EncryptStrategyFactory;

@Service
public class EncryptStrategyFactoryImpl extends AbstractCodeServiceFactory<EncryptStrategy>
    implements EncryptStrategyFactory {

    public EncryptStrategyFactoryImpl(List<EncryptStrategy> encryptStrategies) {
        super(encryptStrategies);
    }

    @Override
    protected String getFactoryName() {
        return "加密策略工厂";
    }

}
