package bacta.io.launcher.cli;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * Created by crush on 7/4/2017.
 */
@Configuration
@ConfigurationProperties
public class ClientLauncherApplicationConfiguration {
    @Bean
    public PrintStream getPrintStream() {
        return System.out;
    }

    @Bean public InputStream getInputStream() {
        return System.in;
    }
}
