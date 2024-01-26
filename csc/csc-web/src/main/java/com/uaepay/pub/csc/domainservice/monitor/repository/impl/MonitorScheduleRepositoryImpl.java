package com.uaepay.pub.csc.domainservice.monitor.repository.impl;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareSchedule;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorSchedule;
import com.uaepay.pub.csc.core.dal.mapper.monitor.MonitorScheduleMapper;
import com.uaepay.pub.csc.domainservice.monitor.repository.MonitorScheduleRepository;
import com.uaepay.pub.csc.service.facade.enums.ScheduleStatusEnum;

/**
 * @author zc
 */
@Repository
public class MonitorScheduleRepositoryImpl implements MonitorScheduleRepository {

    @Autowired
    MonitorScheduleMapper monitorScheduleMapper;

    @Override
    public List<MonitorSchedule> getScheduleToExecute(int beforeMinutes, int shardingIndex, int shardingCount,
        int batchSize) {
        return monitorScheduleMapper.selectToExecute(beforeMinutes, shardingIndex, shardingCount, batchSize);
    }

    @Override
    public void updateTaskApplied(MonitorSchedule schedule, int triggerDelayMinutes) {
        schedule.setNextTriggerTime(DateTime.now().plusMinutes(triggerDelayMinutes).toDate());
        int update = monitorScheduleMapper.updateTaskApplied(schedule.getScheduleId(), schedule.getUpdateVersion(),
            schedule.getCurrentTaskId(), schedule.getNextTriggerTime());
        if (update != 1) {
            throw new ErrorException("任务锁定更新异常");
        }
        schedule.setUpdateVersion(schedule.getUpdateVersion() + 1);
    }

    @Override
    public void updateTaskErrorDelay(MonitorSchedule schedule, boolean increaseErrorCount, int triggerDelayMinutes) {
        if (increaseErrorCount) {
            schedule.setErrorCount(schedule.getErrorCount() + 1);
        }
        schedule.setNextTriggerTime(DateTime.now().plusMinutes(triggerDelayMinutes).toDate());
        int update = monitorScheduleMapper.updateTaskErrorDelay(schedule.getScheduleId(), schedule.getUpdateVersion(),
            schedule.getErrorCount(), schedule.getNextTriggerTime());
        if (update != 1) {
            throw new ErrorException("计划执行异常，延迟更新异常");
        }
        schedule.setUpdateVersion(schedule.getUpdateVersion() + 1);
    }

    @Override
    public void updateTaskErrorReset(MonitorSchedule schedule) {
        schedule.setErrorCount(schedule.getErrorCount() + 1);
        int update = monitorScheduleMapper.updateTaskErrorReset(schedule.getScheduleId(), schedule.getUpdateVersion(),
            schedule.getErrorCount());
        if (update != 1) {
            throw new ErrorException("计划异常，重置更新异常");
        }
        schedule.setUpdateVersion(schedule.getUpdateVersion() + 1);
        schedule.setCurrentTaskId(null);
    }

    @Override
    public void updateTaskErrorDisable(MonitorSchedule schedule) {
        schedule.setErrorCount(schedule.getErrorCount() + 1);
        schedule.setScheduleStatus(ScheduleStatusEnum.ERROR);
        int update = monitorScheduleMapper.updateErrorStatus(schedule.getScheduleId(), schedule.getUpdateVersion(),
            schedule.getErrorCount(), schedule.getScheduleStatus());
        if (update != 1) {
            throw new ErrorException("计划异常，停用更新异常");
        }
        schedule.setUpdateVersion(schedule.getUpdateVersion() + 1);
    }

    @Override
    public void updateTaskNormal(MonitorSchedule schedule) {
        schedule.setErrorCount(0);
        int update = monitorScheduleMapper.updateTaskNormal(schedule.getScheduleId(), schedule.getUpdateVersion(),
            schedule.getErrorCount(), schedule.getCheckedTime(), schedule.getNextTriggerTime());
        if (update != 1) {
            throw new ErrorException("计划正常执行，更新异常");
        }
        schedule.setUpdateVersion(schedule.getUpdateVersion() + 1);
        schedule.setCurrentTaskId(null);
    }

}
