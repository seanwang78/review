package com.uaepay.pub.csc.ext.integration.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

public class BaseClient {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${spring.application.name}")
    protected String clientId;

}
