package io.bacta.login.server.config;

import com.codahale.metrics.MetricRegistry;
import io.bacta.engine.network.udp.UdpChannel;
import io.bacta.login.server.LoginServerProperties;
import io.bacta.soe.config.SoeNetworkConfiguration;
import io.bacta.soe.network.channel.BroadcastService;
import io.bacta.soe.network.channel.SoeMessageChannel;
import io.bacta.soe.network.connection.SoeUdpConnectionCache;
import io.bacta.soe.network.dispatch.GameNetworkMessageControllerLoader;
import io.bacta.soe.network.dispatch.SoeDevMessageHandler;
import io.bacta.soe.network.dispatch.SoeMessageHandler;
import io.bacta.soe.network.handler.DefaultGameNetworkMessageHandler;
import io.bacta.soe.network.handler.GameNetworkMessageHandler;
import io.bacta.soe.network.protocol.SoeProtocolHandler;
import io.bacta.soe.network.relay.AkkaGameNetworkMessageRelay;
import io.bacta.soe.network.relay.GameNetworkMessageRelay;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

@Configuration
public class SoeChannelConfiguration {

    private final LoginServerProperties properties;

    @Inject
    public SoeChannelConfiguration(final LoginServerProperties properties) {
        this.properties = properties;
    }

    @Bean(name = "ServerChannel")
    @Inject
    public SoeMessageChannel getServerMessageChannel(final ApplicationContext context,
                                                     final UdpChannel udpChannel,
                                                     final SoeProtocolHandler protocolHandler,
                                                     final SoeUdpConnectionCache connectionCache,
                                                     final SoeNetworkConfiguration networkConfiguration,
                                                     final ApplicationEventPublisher publisher,
                                                     final BroadcastService broadcastService,
                                                     final MetricRegistry metricRegistry) {

        final SoeMessageHandler soeMessageHandler = new SoeDevMessageHandler();

        final GameNetworkMessageHandler gameNetworkMessageHandler = new DefaultGameNetworkMessageHandler(GameNetworkMessageControllerLoader.loadControllers(context));
        final GameNetworkMessageRelay gameNetworkMessageRelay = new AkkaGameNetworkMessageRelay(connectionCache, gameNetworkMessageHandler);

        SoeMessageChannel soeMessageChannel = new SoeMessageChannel(udpChannel, protocolHandler, connectionCache, soeMessageHandler, networkConfiguration, gameNetworkMessageRelay, publisher, broadcastService, metricRegistry);
        soeMessageChannel.configure("Login", properties.getBindAddress(), properties.getBindPort());
        return soeMessageChannel;
    }

}
