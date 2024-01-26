package com.uaepay.pub.csc.domain.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author zc
 */
@Data
@ConfigurationProperties(prefix = "log-monitor")
public class LogMonitorProperties {

    /** 分页查询大小 */
    int queryPageSize = 300;

}
