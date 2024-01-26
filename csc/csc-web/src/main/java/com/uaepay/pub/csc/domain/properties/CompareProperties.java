package com.uaepay.pub.csc.domain.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * @author zc
 */
@Data
@ConfigurationProperties(prefix = "compare")
public class CompareProperties {

    /**
     * 查询超时时间，单位: 秒
     */
    int queryTimeOut;

    /** 分页查询大小 */
    int queryPageSize = 100;

    /** 查询目标数据大小限制 */
    int queryTargetSize = 1000;

    /** 最大异常明细数量 */
    int maxErrorDetailCount = 200;

    /** 线程池-核心线程数 */
    int corePoolSize = 50;

    /** 线程池-最大线程数 */
    int maxPoolSize = 50;

    /** 线程池-队列大小 */
    int queueCapacity = 100;

    /** 线程池-线程空闲时间 */
    int keepAliveSeconds = 100;

}
