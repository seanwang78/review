package com.uaepay.pub.csc.domainservice.compare.schedule;

/**
 * 计划任务服务
 * 
 * @author zc
 */
public interface CompareScheduleJobService {

    void runJob(int shardingIndex, int shardingCount);

}
