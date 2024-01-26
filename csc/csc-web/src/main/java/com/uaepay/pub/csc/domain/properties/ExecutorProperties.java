package com.uaepay.pub.csc.domain.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author zc
 */
@Data
@ConfigurationProperties(prefix = "executor")
public class ExecutorProperties {

    /** 线程池-核心线程数 */
    int corePoolSize = 5;

    /** 线程池-最大线程数 */
    int maxPoolSize = 5;

    /** 线程池-队列大小 */
    int queueCapacity = 10;

    /** 线程池-线程空闲时间 */
    int keepAliveSeconds = 100;

}
