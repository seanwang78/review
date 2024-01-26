package com.uaepay.pub.csc.domain.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "schedule")
public class ScheduleProperties {

    /**
     * 距离当前时间分钟数
     */
    int beforeMinutes = 1440;

    /**
     * 批次大小
     */
    int batchSize = 100;

    /**
     * 任务已满时，计划延迟执行时长
     */
    int taskFullDelayMinutes = 5;

    /**
     * 异常时，计划延迟执行时长
     */
    int errorDelayMinutes = 15;

    /**
     * 异常次数到达此值，则改计划为停用
     */
    int errorToDisableCount = 20;

}
