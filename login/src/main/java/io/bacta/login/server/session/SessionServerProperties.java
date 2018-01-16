package io.bacta.login.server.session;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "io.bacta.session.server")
public class SessionServerProperties {
    //private InetAddress bindAddress;
    //private int bindPort;
    private boolean autoAccountCreationEnabled;
    private boolean multipleSessionsPerAccountAllowed;
    private long updateInterval;
}