package io.bacta.scene.config;

import gnu.trove.map.TIntObjectMap;
import io.bacta.soe.network.dispatch.GameNetworkMessageControllerData;
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
        final GameNetworkMessageControllerLoader controllerLoader = new GameNetworkMessageControllerLoader();
        final TIntObjectMap<GameNetworkMessageControllerData> controllers = controllerLoader.loadControllers(context);
        return new DefaultGameNetworkMessageHandler(controllers);
    }
}
