package com.ocdsoft.bacta.swg.login;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;

/**
 * Created by kyle on 6/29/2017.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "soe.server.login")
public class LoginServerProperties {
    private InetAddress bindAddress;
    private int bindPort;
}
