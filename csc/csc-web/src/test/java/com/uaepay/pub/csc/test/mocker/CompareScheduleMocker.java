package com.uaepay.pub.csc.test.mocker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareSchedule;
import com.uaepay.pub.csc.core.dal.mapper.compare.CompareScheduleMapper;
import com.uaepay.pub.csc.test.dal.CompareTestMapper;

@Service
public class CompareScheduleMocker {

    @Autowired
    private CompareTestMapper compareTestMapper;

    @Autowired
    private CompareScheduleMapper compareScheduleMapper;

    public void mock(CompareSchedule schedule) {
        int delete = compareTestMapper.deleteScheduleByDefineId(schedule.getDefineId());
        if (delete != 0) {
            System.out.printf("删除计划: defineId=%d, count=%d\n", schedule.getDefineId(), delete);
        }
        compareScheduleMapper.insertSelective(schedule);
        System.out.printf("准备计划: scheduleId=%d\n", schedule.getScheduleId());
    }

}
