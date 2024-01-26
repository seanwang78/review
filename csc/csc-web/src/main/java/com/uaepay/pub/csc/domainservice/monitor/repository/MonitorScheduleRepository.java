package com.uaepay.pub.csc.domainservice.monitor.repository;

import java.util.List;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorSchedule;

/**
 * 监控计划仓储
 * 
 * @author zc
 */
public interface MonitorScheduleRepository {

    /**
     * 获取启用的可执行的计划
     * 
     * @param beforeMinutes
     *            距离当前时间分钟数
     * @param shardingIndex
     *            分片索引
     * @param shardingCount
     *            分片数
     * @param batchSize
     *            批次大小
     * @return 对账计划配置
     */
    List<MonitorSchedule> getScheduleToExecute(int beforeMinutes, int shardingIndex, int shardingCount, int batchSize);

    /**
     * 任务提交成功，更新任务id
     *
     * @param schedule
     *            计划
     * @param triggerDelayMinutes
     *            下次执行时间相对当前时间延迟，单位：分钟
     */
    void updateTaskApplied(MonitorSchedule schedule, int triggerDelayMinutes);

    /**
     * 任务异常，更新延迟时间
     *
     * @param schedule
     *            计划
     * @param increaseErrorCount
     *            是否增加异常次数
     * @param triggerDelayMinutes
     *            下次执行时间相对当前时间延迟，单位：分钟
     */
    void updateTaskErrorDelay(MonitorSchedule schedule, boolean increaseErrorCount, int triggerDelayMinutes);

    /**
     * 任务异常重置
     *
     * @param schedule
     *            计划
     */
    void updateTaskErrorReset(MonitorSchedule schedule);

    /**
     * 任务异常停用，更新状态为停用
     * 
     * @param schedule
     *            计划
     */
    void updateTaskErrorDisable(MonitorSchedule schedule);

    /**
     * 任务正常执行
     * 
     * @param schedule
     *            计划
     */
    void updateTaskNormal(MonitorSchedule schedule);

}
