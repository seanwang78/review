package com.uaepay.pub.csc.domainservice.monitor.task.action.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.common.util.VelocityUtil;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorDefine;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTask;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTaskDetail;
import com.uaepay.pub.csc.domain.enums.TaskErrorCodeEnum;
import com.uaepay.pub.csc.domain.monitor.MonitorResult;
import com.uaepay.pub.csc.domainservice.monitor.repository.MonitorTaskRepository;
import com.uaepay.pub.csc.domainservice.monitor.task.action.MonitorAction;
import com.uaepay.pub.csc.service.facade.enums.AlarmLevelEnum;

/**
 * 报警操作工厂
 * 
 * @author zc
 */
@Service
public class AlarmActionFactory {

    public AlarmActionFactory(MonitorTaskRepository monitorTaskRepository) {
        this.monitorTaskRepository = monitorTaskRepository;
    }

    MonitorTaskRepository monitorTaskRepository;

    public MonitorAction create() {
        return new AlarmAction();
    }

    public class AlarmAction implements MonitorAction {

        private static final String PARAM_RESULT = "result";

        private final Logger logger = LoggerFactory.getLogger(AlarmAction.class);

        @Override
        public void afterQuery(MonitorDefine define, MonitorTask task, MonitorResult monitorResult) {
            // 计算报警级别
            if (StringUtils.isBlank(define.getNotifyExpression())) {
                if (monitorResult.size() == 0) {
                    task.setAlarmLevel(AlarmLevelEnum.IGNORE);
                } else {
                    task.setAlarmLevel(AlarmLevelEnum.NORMAL);
                }
                return;
            }
            Map<String, Object> params = new HashMap<>(1);
            params.put(PARAM_RESULT, monitorResult);
            try {
                String result = StringUtils.trim(VelocityUtil.getString(define.getNotifyExpression(), params));
                AlarmLevelEnum alarmLevel = AlarmLevelEnum.valueOf(StringUtils.trim(result));
                task.setAlarmLevel(alarmLevel);
            } catch (Throwable e) {
                throw new ErrorException(TaskErrorCodeEnum.NOTIFY_EXPRESSION_ERROR, e.getMessage());
            }
        }

        @Override
        public void updateTask(MonitorDefine define, MonitorTask task, MonitorResult monitorResult) {
            AlarmLevelEnum alarmLevel = task.getAlarmLevel();
            List<MonitorTaskDetail> details = MonitorAction.buildDetails(define, task, monitorResult);
            if (alarmLevel == AlarmLevelEnum.IGNORE) {
                monitorTaskRepository.updateSuccess(task, details);
            } else {
                monitorTaskRepository.updateFail(task, details);
            }
        }

    }

}
