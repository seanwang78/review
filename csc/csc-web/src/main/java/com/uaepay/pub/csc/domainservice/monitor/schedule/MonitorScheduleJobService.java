package com.uaepay.pub.csc.domainservice.monitor.schedule;

/**
 * 计划任务服务
 * 
 * @author zc
 */
public interface MonitorScheduleJobService {

    void runJob(int shardingIndex, int shardingCount);

}
