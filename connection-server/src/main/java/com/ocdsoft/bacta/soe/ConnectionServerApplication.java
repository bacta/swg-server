package com.ocdsoft.bacta.soe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * Created by kyle on 6/29/2017.
 */
@SpringBootApplication
@EnableConfigurationProperties
public class ConnectionServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConnectionServerApplication.class, args);
    }
}
