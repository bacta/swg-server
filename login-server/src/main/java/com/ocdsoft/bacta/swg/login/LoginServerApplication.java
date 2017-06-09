package com.ocdsoft.bacta.swg.login;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * Created by kburkhardt on 2/14/14.
 */

@Slf4j
@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties
@ComponentScan({"com.ocdsoft.bacta.swg.login"})
public class LoginServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoginServerApplication.class, args);
    }

}
