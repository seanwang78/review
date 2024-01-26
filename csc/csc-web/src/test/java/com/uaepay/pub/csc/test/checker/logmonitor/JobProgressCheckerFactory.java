package com.uaepay.pub.csc.test.checker.logmonitor;

import org.joda.time.DateTime;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.JobProgress;
import com.uaepay.pub.csc.core.dal.mapper.logmonitor.JobProgressMapper;

@Repository
public class JobProgressCheckerFactory {

    @Autowired
    JobProgressMapper jobProgressMapper;

    public Checker create(JobProgress jobProgress) {
        JobProgress progress = jobProgressMapper.selectByPrimaryKey(jobProgress.getJobCode());
        return new Checker(progress);
    }

    public static class Checker {

        public Checker(JobProgress progress) {
            this.progress = progress;
        }

        private final JobProgress progress;

        public Checker exist() {
            Assertions.assertNotNull(progress);
            return this;
        }

        public Checker checkedTime(DateTime checkedTime) {
            Assertions.assertEquals(checkedTime.toDate(), progress.getCheckedTime());
            return this;
        }

        public Checker nextTrigger(DateTime nextTriggerTime) {
            Assertions.assertEquals(nextTriggerTime.toDate(), progress.getNextTriggerTime());
            return this;
        }

        public Checker memo(String memo) {
            Assertions.assertEquals(memo, progress.getMemo());
            return this;
        }

        public Checker version(long version) {
            Assertions.assertEquals(version, progress.getUpdateVersion());
            return this;
        }

    }

}
