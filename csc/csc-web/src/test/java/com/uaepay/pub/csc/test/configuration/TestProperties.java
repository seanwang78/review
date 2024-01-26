package com.uaepay.pub.csc.test.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

import java.util.List;

/**
 * @author zc
 */
@Data
@ConfigurationProperties(prefix = "test")
public class TestProperties {

    /**
     * mongo测试库uri
     */
    String mongoTestDb;

    String esUsername;

    String esPassword;

    List<String> esUris;

}
