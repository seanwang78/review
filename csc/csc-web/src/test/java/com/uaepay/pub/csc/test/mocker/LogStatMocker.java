package com.uaepay.pub.csc.test.mocker;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.LogStat;
import com.uaepay.pub.csc.core.dal.mapper.logmonitor.LogStatMapper;
import com.uaepay.pub.csc.test.builder.LogStatBuilder;
import com.uaepay.pub.csc.test.dal.LogStatTestMapper;

@Service
public class LogStatMocker {

    @Autowired
    LogStatMapper logStatMapper;

    @Autowired
    LogStatTestMapper logStatTestMapper;

    List<LogStatBuilder> builders = new ArrayList<>();

    public void clearAll() {
        int count = logStatTestMapper.deleteAll();
        System.out.printf("删除所有cgs日志统计: count=%d\n", count);
    }

    public LogStatMocker reset() {
        this.builders.clear();
        return this;
    }

    public LogStatBuilder addBuilder() {
        LogStatBuilder builder = new LogStatBuilder();
        builders.add(builder);
        return builder;
    }

    public void mock() {
        clearAll();
        for (LogStatBuilder builder : builders) {
            LogStat logStat = builder.build();
            System.out.printf("mock数据: %s\n", logStat);
            logStatMapper.insertSelective(logStat);
        }
    }

}
