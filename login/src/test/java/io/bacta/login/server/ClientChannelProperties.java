package io.bacta.login.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;

@Data
@Configuration
@ConfigurationProperties(prefix = "io.bacta.login.test.client")
public class ClientChannelProperties {
    private InetAddress loginAddress;
    private int loginPort;
    private InetAddress bindAddress;
    private int bindPort;
}
