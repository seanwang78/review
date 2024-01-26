package com.uaepay.pub.csc.domainservice.executor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 人工任务执行器
 * 
 * @author zc
 */
public interface CscNormalTaskExecutor {

    /**
     * 获取线程任务执行器，使用AbortPolicy
     *
     * @return 执行器
     */
    ThreadPoolTaskExecutor getExecutor();

    /**
     * 是否已达到最大线程数
     * 
     * @return 是否已达到
     */
    boolean isFull();

}
