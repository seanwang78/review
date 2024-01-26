package com.uaepay.pub.csc.test.mocker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorDefine;
import com.uaepay.pub.csc.core.dal.dataobject.monitor.MonitorSchedule;
import com.uaepay.pub.csc.core.dal.mapper.monitor.MonitorDefineMapper;
import com.uaepay.pub.csc.core.dal.mapper.monitor.MonitorScheduleMapper;
import com.uaepay.pub.csc.test.builder.MonitorDefineBuilder;
import com.uaepay.pub.csc.test.dal.MonitorTestMapper;

@Service
public class MonitorMocker {

    @Autowired
    private MonitorTestMapper monitorTestMapper;

    @Autowired
    private MonitorDefineMapper monitorDefineMapper;

    @Autowired
    private MonitorScheduleMapper monitorScheduleMapper;

    public long mock(MonitorDefineBuilder defineBuilder) {
        MonitorDefine define = defineBuilder.build();
        Long defineId = monitorTestMapper.selectDefineByName(define.getDefineName());
        if (defineId != null) {
            int delete = monitorTestMapper.deleteDefineById(defineId);
            System.out.printf("删除监控定义: defineName=%s, count=%d\n", define.getDefineName(), delete);
            // delete = monitorTestMapper.deleteScheduleByDefineId(defineId);
            // System.out.printf("删除监控计划: defineName=%s, count=%d\n", define.getDefineName(), delete);
        }
        monitorDefineMapper.insertSelective(define);
        System.out.printf("准备监控定义: %s, id: %d\n", define.getDefineName(), define.getDefineId());
        return define.getDefineId();
    }

    public long getDefine(String defineName) {
        return monitorTestMapper.selectDefineByName(defineName);
    }

    public void mock(MonitorSchedule schedule) {
        int delete = monitorTestMapper.deleteScheduleByDefineId(schedule.getDefineId());
        if (delete != 0) {
            System.out.printf("删除计划: defineId=%d, count=%d\n", schedule.getDefineId(), delete);
        }
        monitorScheduleMapper.insertSelective(schedule);
        System.out.printf("准备计划: scheduleId=%d\n", schedule.getScheduleId());
    }

}
