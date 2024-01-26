package com.uaepay.pub.csc.domainservice.logmonitor.repository;

import java.util.Date;
import java.util.List;

import com.uaepay.pub.csc.core.dal.dataobject.logmonitor.JobProgress;

/**
 * @author zc
 */
public interface JobProgressRepository {

    /**
     * 更新进度
     * 
     * @param jobProgress
     *            进度
     */
    void updateProgress(JobProgress jobProgress);

    /**
     * 更新进度异常
     * 
     * @param jobProgress
     *            进度
     */
    void updateErrorMsg(JobProgress jobProgress);

    /**
     * 获取待执行进度
     * 
     * @param beforeTime
     *            截止时间
     * @return 进度列表
     */
    List<JobProgress> selectToExecute(Date beforeTime);

}
