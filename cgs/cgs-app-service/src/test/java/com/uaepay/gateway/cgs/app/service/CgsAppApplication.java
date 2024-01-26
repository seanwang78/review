package com.uaepay.gateway.cgs.app.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * -javaagent:D:\3rdParty\skywalking\6.3.0\agent\skywalking-agent.jar -Dskywalking.agent.service_name=gp024_cgs
 *
 * @author zc
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class CgsAppApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(CgsAppApplication.class);
        application.run(args);
    }

}
