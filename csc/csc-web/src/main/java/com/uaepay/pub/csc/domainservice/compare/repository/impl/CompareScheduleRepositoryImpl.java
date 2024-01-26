package com.uaepay.pub.csc.domainservice.compare.repository.impl;

import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareSchedule;
import com.uaepay.pub.csc.core.dal.mapper.compare.CompareScheduleMapper;
import com.uaepay.pub.csc.domainservice.compare.repository.CompareScheduleRepository;
import com.uaepay.pub.csc.service.facade.enums.ScheduleStatusEnum;

/**
 * @author zc
 */
@Repository
public class CompareScheduleRepositoryImpl implements CompareScheduleRepository {

    @Autowired
    CompareScheduleMapper compareScheduleMapper;

    @Override
    public List<CompareSchedule> getScheduleToExecute(int beforeMinutes, int shardingIndex, int shardingCount,
        int batchSize) {
        return compareScheduleMapper.selectToExecute(beforeMinutes, shardingIndex, shardingCount, batchSize);
    }

    @Override
    public void updateTaskApplied(CompareSchedule schedule, int triggerDelayMinutes) {
        schedule.setNextTriggerTime(DateTime.now().plusMinutes(triggerDelayMinutes).toDate());
        int update = compareScheduleMapper.updateTaskApplied(schedule.getScheduleId(), schedule.getVersion(),
            schedule.getCurrentTaskId(), schedule.getNextTriggerTime());
        if (update != 1) {
            throw new ErrorException("任务锁定更新异常");
        }
        schedule.setVersion(schedule.getVersion() + 1);
    }

    @Override
    public void updateTaskErrorDelay(CompareSchedule schedule, boolean increaseErrorCount, int triggerDelayMinutes) {
        if (increaseErrorCount) {
            schedule.setErrorCount(schedule.getErrorCount() + 1);
        }
        schedule.setNextTriggerTime(DateTime.now().plusMinutes(triggerDelayMinutes).toDate());
        int update = compareScheduleMapper.updateTaskErrorDelay(schedule.getScheduleId(), schedule.getVersion(),
            schedule.getErrorCount(), schedule.getNextTriggerTime());
        if (update != 1) {
            throw new ErrorException("计划执行异常，延迟更新异常");
        }
        schedule.setVersion(schedule.getVersion() + 1);
    }

    @Override
    public void updateTaskErrorReset(CompareSchedule schedule) {
        schedule.setErrorCount(schedule.getErrorCount() + 1);
        int update = compareScheduleMapper.updateTaskErrorReset(schedule.getScheduleId(), schedule.getVersion(),
            schedule.getErrorCount());
        if (update != 1) {
            throw new ErrorException("计划异常，重置更新异常");
        }
        schedule.setVersion(schedule.getVersion() + 1);
        schedule.setCurrentTaskId(null);
    }

    @Override
    public void updateTaskErrorDisable(CompareSchedule schedule) {
        schedule.setErrorCount(schedule.getErrorCount() + 1);
        schedule.setScheduleStatus(ScheduleStatusEnum.ERROR);
        int update = compareScheduleMapper.updateErrorStatus(schedule.getScheduleId(), schedule.getVersion(),
            schedule.getErrorCount(), schedule.getScheduleStatus());
        if (update != 1) {
            throw new ErrorException("计划异常，停用更新异常");
        }
        schedule.setVersion(schedule.getVersion() + 1);
    }

    @Override
    public void updateTaskNormal(CompareSchedule schedule) {
        schedule.setErrorCount(0);
        int update = compareScheduleMapper.updateTaskNormal(schedule.getScheduleId(), schedule.getVersion(),
            schedule.getErrorCount(), schedule.getCheckedTime(), schedule.getNextTriggerTime());
        if (update != 1) {
            throw new ErrorException("计划正常执行，更新异常");
        }
        schedule.setVersion(schedule.getVersion() + 1);
        schedule.setCurrentTaskId(null);
    }

}
