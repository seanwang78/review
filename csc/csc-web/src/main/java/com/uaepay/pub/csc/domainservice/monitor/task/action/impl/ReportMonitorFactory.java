package com.uaepay.pub.csc.domainservice.monitor.task.action.impl;

import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorDefine;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorTask;
import com.uaepay.pub.csc.domain.monitor.MonitorResult;
import com.uaepay.pub.csc.domainservice.monitor.repository.MonitorTaskRepository;
import com.uaepay.pub.csc.domainservice.monitor.task.action.MonitorAction;

/**
 * @author zc
 */
@Service
public class ReportMonitorFactory {

    public ReportMonitorFactory(MonitorTaskRepository monitorTaskRepository) {
        this.monitorTaskRepository = monitorTaskRepository;
    }

    MonitorTaskRepository monitorTaskRepository;

    public MonitorAction create() {
        return new ReportAction();
    }

    public class ReportAction implements MonitorAction {

        @Override
        public void afterQuery(MonitorDefine define, MonitorTask task, MonitorResult monitorResult) {
            // 啥也不做
        }

        @Override
        public void updateTask(MonitorDefine define, MonitorTask task, MonitorResult monitorResult) {
            monitorTaskRepository.updateSuccess(task, MonitorAction.buildDetails(define, task, monitorResult));
        }

    }

}
