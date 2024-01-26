package com.uaepay.pub.csc.domainservice.logmonitor.strategy;

import java.util.Date;
import java.util.List;

import com.uaepay.basis.beacon.service.facade.domain.response.CommonResponse;

/**
 * @author cyx
 */
public interface LogMonitorStrategy {

    /**
     * 策略代码
     * 
     * @return 功能代码
     */
    String getCode();

    /**
     * 分析日志
     * 
     * @param beginDate
     *            开始时间
     * @param endDate
     *            结束时间
     */
    void execute(Date beginDate, Date endDate);

    /**
     * 分析结果重新比对规则
     * 
     * @param beginDate
     *            开始时间
     * @param endDate
     *            结束时间
     * @param operator
     *            操作员
     * @return 执行结果
     */
    CommonResponse retry(Date beginDate, Date endDate, String operator);

    /**
     * 人工确认
     * 
     * @param logStatIdList
     *            日志统计id列表
     * @param operator
     *            操作员
     * @return 执行结果
     */
    CommonResponse manualConfirm(List<Long> logStatIdList, String operator);
}
