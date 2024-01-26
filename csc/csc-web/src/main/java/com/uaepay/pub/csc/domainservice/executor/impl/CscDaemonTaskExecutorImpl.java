package com.uaepay.pub.csc.domainservice.executor.impl;

import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import com.uaepay.pub.csc.domain.properties.CompareProperties;
import com.uaepay.pub.csc.domainservice.executor.CscDaemonTaskExecutor;

/**
 * @author zc
 */
@Service
public class CscDaemonTaskExecutorImpl implements CscDaemonTaskExecutor {

    private static final String THREAD_NAME_PREFIX = "CSC_DAEMON";

    @Autowired
    protected CompareProperties compareProperties;

    protected ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @PostConstruct
    public void init() {
        threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setThreadNamePrefix(THREAD_NAME_PREFIX);
        threadPoolTaskExecutor.setCorePoolSize(compareProperties.getCorePoolSize());
        threadPoolTaskExecutor.setMaxPoolSize(compareProperties.getMaxPoolSize());
        threadPoolTaskExecutor.setQueueCapacity(compareProperties.getQueueCapacity());
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);
        threadPoolTaskExecutor.setKeepAliveSeconds(compareProperties.getKeepAliveSeconds());
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();
    }

    @Override
    public ThreadPoolTaskExecutor getExecutor() {
        return threadPoolTaskExecutor;
    }

}
