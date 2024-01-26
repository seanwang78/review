package com.uaepay.pub.csc.test.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zc
 */
@Configuration
@EnableConfigurationProperties({TestProperties.class})
public class TestPropertiesConfiguration {}
