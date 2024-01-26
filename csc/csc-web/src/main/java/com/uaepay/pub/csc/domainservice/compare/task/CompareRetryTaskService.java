package com.uaepay.pub.csc.domainservice.compare.task;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;

/**
 * 重试任务服务
 * 
 * @author zc
 */
public interface CompareRetryTaskService {

    /**
     * 重试任务
     * 
     * @param origTask
     *            原任务
     * @param operator
     *            操作员
     * @param compensate
     *            是否补单
     * @param daemon
     *            是否后台线程
     * @return 重试任务id，如果申请异常报错
     */
    long retry(CompareTask origTask, String operator, boolean compensate, boolean daemon);

}
