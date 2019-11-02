package io.bacta.connection.config;

import gnu.trove.map.TIntObjectMap;
import io.bacta.soe.network.connection.SoeUdpConnectionCache;
import io.bacta.soe.network.dispatch.GameNetworkMessageControllerData;
import io.bacta.soe.network.dispatch.GameNetworkMessageControllerLoader;
import io.bacta.soe.network.handler.DefaultGameNetworkMessageHandler;
import io.bacta.soe.network.handler.GameNetworkMessageHandler;
import io.bacta.soe.network.relay.AkkaGameNetworkMessageRelay;
import io.bacta.soe.network.relay.GameNetworkMessageRelay;
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
    public GameNetworkMessageRelay gameNetworkMessageForwarder(final SoeUdpConnectionCache connectionCache, final GameNetworkMessageHandler gameNetworkMessageHandler) {
        return new AkkaGameNetworkMessageRelay(connectionCache, gameNetworkMessageHandler);
    }

    @Bean
    @Inject
    public GameNetworkMessageHandler getGameNetworkMessageHandler(final ApplicationContext context) {
        final GameNetworkMessageControllerLoader controllerLoader = new GameNetworkMessageControllerLoader();
        final TIntObjectMap<GameNetworkMessageControllerData> controllers = controllerLoader.loadControllers(context);
        return new DefaultGameNetworkMessageHandler(controllers);
    }
}
