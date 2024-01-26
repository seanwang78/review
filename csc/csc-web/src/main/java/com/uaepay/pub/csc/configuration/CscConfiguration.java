package com.uaepay.pub.csc.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * @author lzb
 */
@Data
@Configuration
@ConfigurationProperties("csc")
public class CscConfiguration {

    /** 附件文件过期间隔 */
    private Duration attachFileExpireDuration = Duration.ofDays(1);
}
