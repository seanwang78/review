package com.uaepay.pub.csc.core.dal.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.jmx.support.RegistrationPolicy;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 注解配置
 * 
 * @author zc
 */
@Configuration
@EnableMBeanExport(registration = RegistrationPolicy.IGNORE_EXISTING)
@ComponentScan("com.uaepay.mysql.starter.plugin")
@EnableTransactionManagement
public class MybatisConfiguration {

}
