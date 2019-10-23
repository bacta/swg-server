package io.bacta.login.server.config;

import io.bacta.soe.util.GameNetworkMessageTemplateWriter;
import io.bacta.soe.util.NullGameNetworkMessageTemplateWriter;
import io.bacta.soe.util.VelocityGameNetworkMessageTemplateWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Slf4j
public class TemplateWriterConfiguration {

    @Bean
    @Profile("dev")
    @SuppressWarnings("squid:S1192")
    public GameNetworkMessageTemplateWriter getWriter() {
        GameNetworkMessageTemplateWriter writer = new VelocityGameNetworkMessageTemplateWriter();
        final Path controllerFilePath = Paths.get("login", "src", "main", "java", "io",
                "bacta", "login", "server", "controller");
        final String controllerPackage = "io.bacta.login.server.controller";
        writer.setControllerInfo(controllerFilePath, controllerPackage);

        final Path messageFilePath = Paths.get("shared", "src", "main", "java", "io",
                "bacta", "login", "message");
        final String messagePackage = "io.bacta.login.message";
        writer.setMessagePackage(messageFilePath, messagePackage);

        return writer;
    }

    @Profile("!dev")
    @Bean
    public GameNetworkMessageTemplateWriter getNullWriter() {
        return new NullGameNetworkMessageTemplateWriter();
    }
}
