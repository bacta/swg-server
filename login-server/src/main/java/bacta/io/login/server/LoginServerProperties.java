package bacta.io.login.server;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;

/**
 * Created by kyle on 6/29/2017.
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "bacta.server.login")
public class LoginServerProperties {
    private InetAddress bindAddress;
    private int publicBindPort;
    private int privateBindPort;
    private String requiredClientVersion;
    private boolean autoGalaxyRegistrationEnabled;
    private boolean internalBypassOnlineLimitEnabled;
    private boolean skippingTutorialAllowedForAll;
    private boolean validateClientVersionEnabled;
    private int populationExtremelyHeavyThresholdPercent;
    private int populationVeryHeavyThresholdPercent;
    private int populationHeavyThresholdPercent;
    private int populationMediumThresholdPercent;
    private int populationLightThresholdPercent;
}
