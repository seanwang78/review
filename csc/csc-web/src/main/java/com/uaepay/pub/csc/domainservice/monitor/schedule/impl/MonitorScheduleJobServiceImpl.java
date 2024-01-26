package com.uaepay.pub.csc.domainservice.monitor.schedule.impl;

import java.util.List;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorSchedule;
import com.uaepay.pub.csc.domainservice.monitor.repository.MonitorScheduleRepository;
import com.uaepay.pub.csc.domainservice.monitor.schedule.MonitorScheduleExecuteService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.core.common.exceptions.TaskFullException;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareSchedule;
import com.uaepay.pub.csc.domain.properties.ScheduleProperties;
import com.uaepay.pub.csc.domainservice.compare.repository.CompareScheduleRepository;
import com.uaepay.pub.csc.domainservice.compare.schedule.CompareScheduleExecuteService;
import com.uaepay.pub.csc.domainservice.monitor.schedule.MonitorScheduleJobService;

/**
 * @author zc
 */
@Service
public class MonitorScheduleJobServiceImpl implements MonitorScheduleJobService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    MonitorScheduleRepository monitorScheduleRepository;

    @Autowired
    ScheduleProperties scheduleProperties;

    @Autowired
    MonitorScheduleExecuteService monitorScheduleExecuteService;

    @Override
    public void runJob(int shardingIndex, int shardingCount) {
        List<MonitorSchedule> schedules = monitorScheduleRepository.getScheduleToExecute(
            scheduleProperties.getBeforeMinutes(), shardingIndex, shardingCount, scheduleProperties.getBatchSize());
        if (CollectionUtils.isEmpty(schedules)) {
            logger.info("无可执行计划");
            return;
        }
        for (MonitorSchedule schedule : schedules) {
            try {
                monitorScheduleExecuteService.execute(schedule);
            } catch (TaskFullException e) {
                logger.error("任务繁忙, 计划终止执行");
                break;
            }
        }
    }
}
