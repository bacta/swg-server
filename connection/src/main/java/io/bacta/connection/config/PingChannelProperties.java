package io.bacta.connection.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;

@Data
@Configuration
@ConfigurationProperties(prefix = "io.bacta.ping")
public class PingChannelProperties {
    private InetAddress bindAddress;
    private int bindPort;
}
