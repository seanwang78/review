package com.uaepay.gateway.cgs.integration.app.impl;

import java.util.concurrent.ConcurrentHashMap;

import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ConsumerConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.gateway.cgs.app.facade.api.ClientGatewayFacade;
import com.uaepay.gateway.cgs.integration.app.AppServiceFactory;

@Service
public class AppServiceFactoryImpl implements AppServiceFactory {

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private ConsumerConfig consumerConfig;

    private final ConcurrentHashMap<String, ClientGatewayFacade> serviceMap = new ConcurrentHashMap<>();

    @Override
    public ClientGatewayFacade getOrCreate(String appCode) {
        ClientGatewayFacade service = serviceMap.get(appCode);
        if (service != null) {
            return service;
        }
        synchronized (serviceMap) {
            service = serviceMap.get(appCode);
            if (service != null) {
                return service;
            }
            service = buildService(appCode);
            serviceMap.put(appCode, service);
        }
        return service;
    }

    public ClientGatewayFacade buildService(String appCode) {
        ReferenceConfig<ClientGatewayFacade> reference = new ReferenceConfig<>();
        reference.setApplication(applicationConfig);
        reference.setConsumer(consumerConfig);
        reference.setInterface(ClientGatewayFacade.class.getName());
        reference.setVersion(appCode);
        return reference.get();
    }

}
