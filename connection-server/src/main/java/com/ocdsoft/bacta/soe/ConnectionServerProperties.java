package com.ocdsoft.bacta.soe;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;

/**
 * Created by kyle on 6/29/2017.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "soe.server.connection")
public class ConnectionServerProperties {
    private InetAddress loginBindAddress;
    private int loginBindPort;
    private InetAddress gameBindAddress;
    private int gameBindPort;
}
