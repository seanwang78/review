package com.uaepay.pub.csc.domainservice.compare.schedule.impl;

import java.util.List;

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
import com.uaepay.pub.csc.domainservice.compare.schedule.CompareScheduleJobService;

/**
 * @author zc
 */
@Service
public class CompareScheduleJobServiceImpl implements CompareScheduleJobService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    CompareScheduleRepository compareScheduleRepository;

    @Autowired
    ScheduleProperties scheduleProperties;

    @Autowired
    CompareScheduleExecuteService compareScheduleExecuteService;

    @Override
    public void runJob(int shardingIndex, int shardingCount) {
        List<CompareSchedule> schedules = compareScheduleRepository.getScheduleToExecute(
            scheduleProperties.getBeforeMinutes(), shardingIndex, shardingCount, scheduleProperties.getBatchSize());
        if (CollectionUtils.isEmpty(schedules)) {
            logger.info("无可执行计划");
            return;
        }
        for (CompareSchedule schedule : schedules) {
            try {
                compareScheduleExecuteService.execute(schedule);
            } catch (TaskFullException e) {
                logger.error("任务繁忙, 计划终止执行");
                break;
            }
        }
    }
}
