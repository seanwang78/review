/**   
 * Copyright © 2019 42PAY. All rights reserved.
 * 
 * @Title: CgsExecutorConfig.java 
 * @Prject: cgs-web
 * @Package: com.uaepay.gateway.cgs.configuration 
 * @author: heyang   
 * @date: Dec 27, 2019 3:45:00 PM 
 * @version: V1.0   
 */
package com.uaepay.gateway.cgs.configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/** 
 * @ClassName: CgsExecutorConfig 
 * @Description: 线程池配置
 * @author: heyang
 * @date: Dec 27, 2019 3:45:00 PM  
 */
@Configuration
@EnableAsync
public class CgsExecutorConfig {
	@Bean
    public Executor asyncServiceExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        //核心线程数
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.setAllowCoreThreadTimeOut(true);
        //最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(5);
        //配置队列大小
        threadPoolTaskExecutor.setQueueCapacity(50);
        //配置线程池前缀
        threadPoolTaskExecutor.setThreadNamePrefix("cgsasync-service-");
        //拒绝策略
        // setRejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
        // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在的线程来执行
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
