package bacta.io.conf;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.InetAddress;

/**
 * Created by kyle on 4/12/2016.
 */
@Data
@ConfigurationProperties(prefix = "bacta.network")
public class NetworkConfigurationImpl implements NetworkConfiguration {
    private InetAddress bindAddress;
    private int bindPort;
}
