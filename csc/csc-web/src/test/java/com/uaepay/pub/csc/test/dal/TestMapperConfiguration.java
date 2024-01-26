package com.uaepay.pub.csc.test.dal;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan(basePackages = "com.uaepay.pub.csc.test.dal", sqlSessionFactoryRef = "sqlSessionFactory")
public class TestMapperConfiguration {

}
