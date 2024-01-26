package com.uaepay.pub.csc.domainservice.logmonitor.repository.impl;

import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uaepay.basis.beacon.common.exception.ErrorException;
import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.JobProgress;
import com.uaepay.pub.csc.core.dal.mapper.logmonitor.JobProgressMapper;
import com.uaepay.pub.csc.domainservice.logmonitor.repository.JobProgressRepository;

/**
 * @author cyx
 */
@Repository
public class JobProgressRepositoryImpl implements JobProgressRepository {

    @Autowired
    JobProgressMapper jobProgressMapper;

    @Override
    public void updateProgress(JobProgress jobProgress) {
        JobProgress forUpdate = new JobProgress();
        forUpdate.setJobCode(jobProgress.getJobCode());
        forUpdate.setCheckedTime(jobProgress.getCheckedTime());
        forUpdate.setNextTriggerTime(jobProgress.getNextTriggerTime());
        forUpdate.setUpdateTime(new Date());
        forUpdate.setUpdateVersion(jobProgress.getUpdateVersion());
        int count = jobProgressMapper.updateByPrimaryKeySelective(forUpdate);
        if (count == 1) {
            jobProgress.setUpdateVersion(jobProgress.getUpdateVersion() + 1);
        } else {
            throw new ErrorException("进度更新失败");
        }
    }

    @Override
    public void updateErrorMsg(JobProgress jobProgress) {
        JobProgress forUpdate = new JobProgress();
        forUpdate.setJobCode(jobProgress.getJobCode());
        forUpdate.setMemo(jobProgress.getMemo());
        forUpdate.setUpdateTime(DateTime.now().toDate());
        forUpdate.setUpdateVersion(jobProgress.getUpdateVersion());
        int count = jobProgressMapper.updateByPrimaryKeySelective(forUpdate);
        if (count == 1) {
            jobProgress.setUpdateVersion(jobProgress.getUpdateVersion() + 1);
        } else {
            throw new ErrorException("进度异常更新失败");
        }
    }

    @Override
    public List<JobProgress> selectToExecute(Date beforeTime) {
        return jobProgressMapper.selectToExecute(beforeTime);
    }

}
