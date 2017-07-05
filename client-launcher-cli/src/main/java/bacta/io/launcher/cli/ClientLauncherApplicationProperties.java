package bacta.io.launcher.cli;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by crush on 7/4/2017.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "bacta.client.launcher.cli")
public class ClientLauncherApplicationProperties {
    private String sessionServerAddress;
    private String clientPath;
    private String username;
    private String password;
}