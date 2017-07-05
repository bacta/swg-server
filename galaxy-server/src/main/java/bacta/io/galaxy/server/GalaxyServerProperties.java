package bacta.io.galaxy.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;

/**
 * Created by kyle on 6/29/2017.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "bacta.server.galaxy")
public class GalaxyServerProperties {
    private InetAddress bindAddress;
    private int publicBindPort;
    private int privateBindPort;
}
