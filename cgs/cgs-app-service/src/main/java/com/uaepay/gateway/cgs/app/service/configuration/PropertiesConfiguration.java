package com.uaepay.gateway.cgs.app.service.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.uaepay.gateway.cgs.app.service.domain.properties.H5AppProperties;

/**
 * @author zc
 */
@Configuration
@EnableConfigurationProperties({H5AppProperties.class})
public class PropertiesConfiguration {}
