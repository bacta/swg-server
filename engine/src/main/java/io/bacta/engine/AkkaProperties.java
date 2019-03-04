package io.bacta.engine;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "io.bacta.akka")
public class AkkaProperties {
    private String clusterName;
    private String config;
}
