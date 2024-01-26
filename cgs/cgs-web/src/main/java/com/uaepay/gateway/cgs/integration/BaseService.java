package com.uaepay.gateway.cgs.integration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public abstract class BaseService {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    public static final String GATEWAY_TYPE = "CGS";

    @Value("${spring.application.name}")
    protected String clientId;

}
