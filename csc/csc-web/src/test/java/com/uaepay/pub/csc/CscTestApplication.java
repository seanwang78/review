package com.uaepay.pub.csc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, MongoAutoConfiguration.class})
@ImportResource("classpath:spring/datasource-unittest.xml")
public class CscTestApplication {

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(CscTestApplication.class);
        application.run(args);
    }

}
