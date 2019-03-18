package io.bacta.login.server.config;

import io.bacta.soe.util.GameNetworkMessageTemplateWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
@Slf4j
public class CodeTemplateConfig {

    @Bean
    @Profile("dev")
    public GameNetworkMessageTemplateWriter getWriter() {
        GameNetworkMessageTemplateWriter writer = new GameNetworkMessageTemplateWriter();
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
        LOGGER.info("Not loading GameNetworkMessageTemplateWriter since 'dev' profile not active");
        return null;
    }
}
