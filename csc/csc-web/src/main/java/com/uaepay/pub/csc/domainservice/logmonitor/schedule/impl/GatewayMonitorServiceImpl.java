package com.uaepay.pub.csc.domainservice.logmonitor.schedule.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.basis.beacon.common.exception.FailException;
import com.uaepay.basis.beacon.service.facade.enums.common.CommonReturnCode;
import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.JobProgress;
import com.uaepay.pub.csc.domainservice.logmonitor.repository.JobProgressRepository;
import com.uaepay.pub.csc.domainservice.logmonitor.schedule.GatewayMonitorService;
import com.uaepay.pub.csc.domainservice.logmonitor.strategy.LogMonitorStrategy;
import com.uaepay.pub.csc.domainservice.logmonitor.strategy.LogMonitorStrategyFactory;

import lombok.extern.slf4j.Slf4j;

/**
 * @author caoyongxing
 */
@Slf4j
@Service
public class GatewayMonitorServiceImpl implements GatewayMonitorService {

    static final Integer ONE_DAY_MINUTES = 60 * 24;

    @Autowired
    JobProgressRepository jobProgressRepository;

    @Autowired
    LogMonitorStrategyFactory logMonitorStrategyFactory;

    @Override
    public void runJob() {
        List<JobProgress> progressList = jobProgressRepository.selectToExecute(DateTime.now().toDate());
        if (CollectionUtils.isEmpty(progressList)) {
            log.info("无可执行任务");
            return;
        }
        // 推进进度
        for (JobProgress progress : progressList) {
            try {
                while (progress.getNextTriggerTime().compareTo(DateTime.now().toDate()) <= 0) {
                    Date beginTime = progress.getCheckedTime();
                    Date endTime = getEndTime(beginTime, progress.getCheckMinutes());
                    checkConfigError(progress, endTime);

                    executeJob(progress, beginTime, endTime);
                    updateProgress(progress, endTime);
                }
            } catch (FailException e) {
                log.info("任务执行失败: {}-{}, progress: {}", e.getCode(), e.getMessage(), progress);
                updateErrorMsg(progress, e.getMessage());
            } catch (ErrorException e) {
                log.error("任务执行异常: {}-{}, progress: {}", e.getCode(), e.getMessage(), progress);
                updateErrorMsg(progress, e.getMessage());
            } catch (Throwable e) {
                log.error("任务执行异常: " + progress.toString(), e);
                updateErrorMsg(progress, e.getMessage());
            }
        }
    }

    private Date getEndTime(Date beginTime, Integer checkMinutes) {
        if (checkMinutes > ONE_DAY_MINUTES) {
            throw new ErrorException(CommonReturnCode.CONFIG_ERROR, "checkMinutes 不能跨天");
        }
        DateTime begin = new DateTime(beginTime);
        DateTime end = begin.plusMinutes(checkMinutes);
        // 跨天，直接把时分秒清零
        if (end.getDayOfMonth() != begin.getDayOfMonth()) {
            end = end.withMillisOfDay(0);
        }
        return end.toDate();
    }

    private void checkConfigError(JobProgress progress, Date endTime) {
        Date minTriggerTime = DateUtils.addMinutes(endTime, progress.getDelayMinutes());
        if (DateTime.now().toDate().before(minTriggerTime)) {
            throw new ErrorException(CommonReturnCode.CONFIG_ERROR, "min trigger time not arrived");
        }
    }

    private void executeJob(JobProgress progress, Date beginTime, Date endTime) {
        LogMonitorStrategy logMonitorStrategy = logMonitorStrategyFactory.getByCode(progress.getJobCode());
        if (logMonitorStrategy == null) {
            throw new ErrorException(CommonReturnCode.CONFIG_ERROR, progress.getJobCode() + " jobCode is not valid");
        }
        logMonitorStrategy.execute(beginTime, endTime);
    }

    private void updateProgress(JobProgress progress, Date endTime) {
        progress.setCheckedTime(endTime);
        progress.setNextTriggerTime(new DateTime(endTime).plusMinutes(progress.getCheckMinutes())
            .plusMinutes(progress.getDelayMinutes()).toDate());
        progress.setMemo("");
        jobProgressRepository.updateProgress(progress);
        log.info("更新进度: {}, {}", progress.getJobCode(),
            DateFormatUtils.format(progress.getCheckedTime(), "yyyy-MM-dd HH:mm"));
    }

    private void updateErrorMsg(JobProgress progress, String message) {
        progress.setMemo(StringUtils.abbreviate(message, 90));
        jobProgressRepository.updateErrorMsg(progress);
    }
}
