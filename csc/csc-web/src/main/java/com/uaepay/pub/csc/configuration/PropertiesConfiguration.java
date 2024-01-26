package com.uaepay.pub.csc.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.uaepay.pub.csc.domain.properties.*;

/**
 * @author zc
 */
@Configuration
@EnableConfigurationProperties({DataSourceProperties.class, CompareProperties.class, ScheduleProperties.class,
    NotifyTemplateProperties.class, MonitorProperties.class, LogMonitorProperties.class, ExecutorProperties.class})
public class PropertiesConfiguration {}
