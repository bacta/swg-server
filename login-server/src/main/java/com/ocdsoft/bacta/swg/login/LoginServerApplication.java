package com.ocdsoft.bacta.swg.login;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * Created by kburkhardt on 2/14/14.
 */

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan("com.ocdsoft.bacta")
@PropertySources({
        @PropertySource("classpath:soenetworking.properties"),
        @PropertySource("classpath:application.properties")

})
public class LoginServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoginServerApplication.class, args);
    }

}
