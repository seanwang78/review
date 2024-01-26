package com.uaepay.gateway.cgs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author zc
 */
@SpringBootApplication
@EnableRedisHttpSession(redisNamespace = "cgs:session")
public class CgsTestApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(CgsTestApplication.class);
        application.run(args);
    }

}
