package com.uaepay.pub.csc.domainservice.executor;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 后台任务执行器
 * 
 * @author zc
 */
public interface CscDaemonTaskExecutor {

    /**
     * 获取线程任务执行器，使用CallerRunPolicy
     * 
     * @return 执行器
     */
    ThreadPoolTaskExecutor getExecutor();

}
