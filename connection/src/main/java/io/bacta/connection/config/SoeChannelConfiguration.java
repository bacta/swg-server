package io.bacta.connection.config;

import io.bacta.soe.network.channel.BroadcastService;
import io.bacta.soe.network.channel.DefaultBroadcastService;
import io.bacta.soe.network.channel.SoeMessageChannel;
import io.bacta.soe.network.connection.SoeUdpConnectionCache;
import io.bacta.soe.network.dispatch.GameNetworkMessageControllerLoader;
import io.bacta.soe.network.dispatch.SoeDevMessageHandler;
import io.bacta.soe.network.dispatch.SoeMessageHandler;
import io.bacta.soe.network.handler.DefaultGameNetworkMessageHandler;
import io.bacta.soe.network.handler.GameNetworkMessageHandler;
import io.bacta.soe.network.relay.AkkaGameNetworkMessageRelay;
import io.bacta.soe.network.relay.GameNetworkMessageRelay;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

@Configuration
public class SoeChannelConfiguration {

    private final ConnectionChannelProperties properties;

    @Inject
    public SoeChannelConfiguration(final ConnectionChannelProperties properties) {
        this.properties = properties;
    }

    @Bean
    public SoeMessageHandler getSoeMessageHandler() {
        return new SoeDevMessageHandler();
    }

    @Bean
    @Inject
    public GameNetworkMessageRelay gameNetworkMessageForwarder(final SoeUdpConnectionCache connectionCache, final GameNetworkMessageHandler gameNetworkMessageHandler) {
        return new AkkaGameNetworkMessageRelay(connectionCache, gameNetworkMessageHandler);
    }

    @Bean
    @Inject
    public GameNetworkMessageHandler getGameNetworkMessageHandler(final ApplicationContext context) {
        return new DefaultGameNetworkMessageHandler(GameNetworkMessageControllerLoader.loadControllers(context));
    }

    @Bean(name = "ServerChannel")
    @Inject
    public SoeMessageChannel getServerMessageChannel(final SoeMessageChannel messageChannel) {
        messageChannel.configure("Connection", properties.getBindAddress(), properties.getBindPort());
        return messageChannel;
    }

    @Bean
    public BroadcastService getBroadcastService() {
        return new DefaultBroadcastService();
    }
}
