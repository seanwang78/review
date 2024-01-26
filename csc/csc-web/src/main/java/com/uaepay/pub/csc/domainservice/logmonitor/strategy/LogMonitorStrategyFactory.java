package com.uaepay.pub.csc.domainservice.logmonitor.strategy;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author cyx
 */
@Component
public class LogMonitorStrategyFactory implements ApplicationContextAware {

    Map<String, LogMonitorStrategy> logMonitorStrategyMap = new HashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, LogMonitorStrategy> beans = applicationContext.getBeansOfType(LogMonitorStrategy.class);
        for (Map.Entry<String, LogMonitorStrategy> item : beans.entrySet()) {
            logMonitorStrategyMap.put(item.getValue().getCode(), item.getValue());
        }
    }

    public LogMonitorStrategy getByCode(String code) {
        return logMonitorStrategyMap.get(code);
    }
}
