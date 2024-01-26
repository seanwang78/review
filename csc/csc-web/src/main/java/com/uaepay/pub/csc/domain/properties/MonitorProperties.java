package com.uaepay.pub.csc.domain.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.uaepay.pub.csc.service.facade.enums.MonitorTypeEnum;

import lombok.Data;

/**
 * @author zc
 */
@Data
@ConfigurationProperties(prefix = "monitor")
public class MonitorProperties {

    /** 最大报表明细数量 */
    int maxReportDetailCount = 500;

    /** 最大报警明细数量 */
    int maxAlarmDetailCount = 200;

    public int getMaxDetailCount(MonitorTypeEnum monitorType) {
        if (monitorType == MonitorTypeEnum.REPORT) {
            return maxReportDetailCount;
        } else {
            return maxAlarmDetailCount;
        }
    }

}
