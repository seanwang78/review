package com.uaepay.pub.csc.domainservice.compare.task;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareDefine;
import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;
import com.uaepay.pub.csc.domain.compare.CompareResult;

/**
 * @author zc
 */
public interface CompareTaskNotifyService {

    /**
     * 通知对账结果
     * 
     * @param task
     *            对账任务
     * @param define
     *            对账定义
     * @param result
     *            对账结果
     * @return
     */
    String notifyCompareResult(CompareTask task, CompareDefine define, CompareResult result);

}
