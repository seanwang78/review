package com.uaepay.gateway.cgs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * -javaagent:D:\3rdParty\skywalking\6.3.0\agent\skywalking-agent.jar -Dskywalking.agent.service_name=gp024_cgs
 *
 * @author zc
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableRedisHttpSession(redisNamespace = "cgs:session")
public class CgsApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(CgsApplication.class);
        application.run(args);
    }

}
