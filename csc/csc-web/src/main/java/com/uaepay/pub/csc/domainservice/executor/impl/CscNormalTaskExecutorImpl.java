package com.uaepay.pub.csc.domainservice.executor.impl;

import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.domain.properties.ExecutorProperties;
import com.uaepay.pub.csc.domainservice.executor.CscNormalTaskExecutor;

/**
 * @author zc
 */
@Service
public class CscNormalTaskExecutorImpl implements CscNormalTaskExecutor {

    private static final String THREAD_NAME_PREFIX = "CSC_NORMAL";

    @Autowired
    protected ExecutorProperties executorProperties;

    protected ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @PostConstruct
    public void init() {
        threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        threadPoolTaskExecutor.setCorePoolSize(executorProperties.getCorePoolSize());
        threadPoolTaskExecutor.setMaxPoolSize(executorProperties.getMaxPoolSize());
        threadPoolTaskExecutor.setQueueCapacity(executorProperties.getQueueCapacity());
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);
        threadPoolTaskExecutor.setKeepAliveSeconds(executorProperties.getKeepAliveSeconds());
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        threadPoolTaskExecutor.initialize();
    }

    @Override
    public ThreadPoolTaskExecutor getExecutor() {
        return threadPoolTaskExecutor;
    }

    @Override
    public boolean isFull() {
        return threadPoolTaskExecutor.getActiveCount() == threadPoolTaskExecutor.getMaxPoolSize();
    }

}
