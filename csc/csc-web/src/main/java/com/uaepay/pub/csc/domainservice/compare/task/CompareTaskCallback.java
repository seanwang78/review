package com.uaepay.pub.csc.domainservice.compare.task;

import com.uaepay.pub.csc.core.dal.dataobject.compare.CompareTask;
import com.uaepay.pub.csc.domain.compare.CompareResult;

/**
 * 对账任务回调
 * 
 * @author zc
 */
public interface CompareTaskCallback {

    // /**
    // * 对账实际开始执行前校验是否执行
    // *
    // * @param task
    // * 对账任务
    // * @return 是否校验通过
    // */
    // boolean checkStart(CompareTask task);

    /**
     * 保存任务同一个事务执行（在保存完任务之后）
     *
     * @param task
     *            任务
     */
    void onSaveTask(CompareTask task);

    /**
     * 是否执行补单相关流程
     * 
     * @return 返回true则执行补单流程，否则不执行
     */
    boolean enableCompensate();

    /**
     * 异常回调
     * 
     * @param e
     *            异常
     */
    void onError(Throwable e);

    /**
     * 对账完成回调
     * 
     * @param task
     *            对账任务
     * @param result
     *            对账结果
     */
    void onResult(CompareTask task, CompareResult result);

}
