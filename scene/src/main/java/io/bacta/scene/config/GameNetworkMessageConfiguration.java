package io.bacta.scene.config;

import io.bacta.soe.network.dispatch.GameNetworkMessageControllerLoader;
import io.bacta.soe.network.handler.DefaultGameNetworkMessageHandler;
import io.bacta.soe.network.handler.GameNetworkMessageHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

@Configuration
@Slf4j
public class GameNetworkMessageConfiguration {

    @Bean
    @Inject
    public GameNetworkMessageHandler getGameNetworkMessageHandler(final ApplicationContext context) {
        return new DefaultGameNetworkMessageHandler(GameNetworkMessageControllerLoader.loadControllers(context));
    }
}
