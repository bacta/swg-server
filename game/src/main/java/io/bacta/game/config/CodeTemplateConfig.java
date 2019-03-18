package io.bacta.game.config;

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

        // Game Network Messages
        GameNetworkMessageTemplateWriter writer = new GameNetworkMessageTemplateWriter();
        final Path controllerFilePath = Paths.get("game", "src", "main", "java", "io",
                "bacta", "game", "controllers");
        final String controllerPackage = "io.bacta.game.controllers";
        writer.setControllerInfo(controllerFilePath, controllerPackage);

        final Path messageFilePath = Paths.get("shared", "src", "main", "java", "io",
                "bacta", "game", "message");
        final String messagePackage = "io.bacta.game.message";
        writer.setMessagePackage(messageFilePath, messagePackage);

        // Obj Messages
        final Path objControllerFilePath = controllerFilePath.resolve("object");
        final String objControllerPackage = "io.bacta.game.controllers.object";
        writer.setObjectControllerInfo(objControllerFilePath, objControllerPackage);

        final Path objMessageFilePath = messageFilePath.resolve("object");
        final String objMessagePackage = "io.bacta.game.message.object";
        writer.setObjMessagePackage(objMessageFilePath, objMessagePackage);

        // Command Messages
        final Path commandControllerFilePath = objControllerFilePath.resolve("command");
        final String commandControllerPackage = "io.bacta.game.controllers.object.command";
        writer.setCommandControllerInfo(commandControllerFilePath, commandControllerPackage);

        return writer;
    }

    @Profile("!dev")
    @Bean
    public GameNetworkMessageTemplateWriter getNullWriter() {
        LOGGER.info("Not loading GameNetworkMessageTemplateWriter since 'dev' profile not active");
        return null;
    }
}
