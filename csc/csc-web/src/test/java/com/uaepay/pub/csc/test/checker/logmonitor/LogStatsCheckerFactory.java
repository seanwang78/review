package com.uaepay.pub.csc.test.checker.logmonitor;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.LogStat;
import com.uaepay.pub.csc.test.dal.LogStatTestMapper;

@Repository
public class LogStatsCheckerFactory {

    @Autowired
    LogStatTestMapper logStatTestMapper;

    public Checker create() {
        return new Checker(logStatTestMapper.selectAll());
    }

    public static class Checker {

        public Checker(List<LogStat> infos) {
            this.infos = infos;
        }

        List<LogStat> infos;

        public Checker count(int expectCount) {
            Assertions.assertEquals(expectCount, infos.size());
            return this;
        }

        public LogStatChecker index(int index) {
            return new LogStatChecker(infos.get(index));
        }

    }

}
