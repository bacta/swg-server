package com.ocdsoft.bacta.soe;

import com.codahale.metrics.MetricRegistry;
import com.ocdsoft.bacta.engine.network.udp.UdpChannel;
import com.ocdsoft.bacta.engine.network.udp.UdpReceiver;
import com.ocdsoft.bacta.soe.config.SoeNetworkConfiguration;
import com.ocdsoft.bacta.soe.network.SoeEncryption;
import com.ocdsoft.bacta.soe.network.XOREncryption;
import com.ocdsoft.bacta.soe.network.connection.SoeConnectionCache;
import com.ocdsoft.bacta.soe.network.connection.SoeConnectionProvider;
import com.ocdsoft.bacta.soe.network.dispatch.ClasspathControllerLoader;
import com.ocdsoft.bacta.soe.network.dispatch.GameNetworkMessageDispatcher;
import com.ocdsoft.bacta.soe.network.dispatch.SoeDevMessageDispatcher;
import com.ocdsoft.bacta.soe.network.dispatch.SoeMessageDispatcher;
import com.ocdsoft.bacta.soe.network.handler.SoeInboundMessageChannel;
import com.ocdsoft.bacta.soe.network.handler.SoeProtocolHandler;
import com.ocdsoft.bacta.soe.serialize.GameNetworkMessageSerializer;
import com.ocdsoft.bacta.soe.serialize.GameNetworkMessageSerializerImpl;
import com.ocdsoft.bacta.soe.service.RandomIntSessionKeyService;
import com.ocdsoft.bacta.soe.service.SessionKeyService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

/**
 * Created by kyle on 7/2/2017.
 */
@Configuration
public class SoeMessageChannelConfiguration implements ApplicationContextAware {

    private final MetricRegistry metricRegistry;
    private final SoeNetworkConfiguration networkConfiguration;
    private ApplicationContext applicationContext;

    @Inject
    public SoeMessageChannelConfiguration(final MetricRegistry metricRegistry,
                                          final SoeNetworkConfiguration networkConfiguration) {
        this.metricRegistry = metricRegistry;
        this.networkConfiguration = networkConfiguration;
    }

    @Bean
    public SessionKeyService getSessionKeyService() {
        return new RandomIntSessionKeyService();
    }

    @Bean
    public GameNetworkMessageSerializer getGameNetworkMessageSerializer() {
        return new GameNetworkMessageSerializerImpl(metricRegistry);
    }

    @Bean
    public SoeConnectionCache getSoeConnectionCache() {
        return new SoeConnectionCache(networkConfiguration, metricRegistry);
    }

    @Bean
    public SoeEncryption getSoeEncryption() {
        return new XOREncryption();
    }

    @Bean
    @Inject
    public GameNetworkMessageDispatcher getGameNetworkMessageDispatcher(final GameNetworkMessageSerializer gameNetworkMessageSerializer) {
        return new GameNetworkMessageDispatcher(
                new ClasspathControllerLoader(applicationContext),
                gameNetworkMessageSerializer,
                null
        );
    }

    @Bean
    @Inject
    public SoeMessageDispatcher getSoeMessageDispatcher(final GameNetworkMessageDispatcher gameNetworkMessageDispatcher) {
        return new SoeDevMessageDispatcher(gameNetworkMessageDispatcher);
    }

    @Bean
    @Inject
    public SoeProtocolHandler getSoeProtocolHandler(final SoeEncryption soeEncryption, final SoeMessageDispatcher soeMessageDispatcher) {
        return new SoeProtocolHandler(
                networkConfiguration,
                soeEncryption,
                soeMessageDispatcher
        );
    }

    @Bean
    @Inject
    public SoeConnectionProvider getSoeConnectionProvider(final GameNetworkMessageSerializer gameNetworkMessageSerializer) {
        return new SoeConnectionProvider(networkConfiguration, gameNetworkMessageSerializer);
    }

    @Bean
    @Inject
    public SoeInboundMessageChannel getSoeInboundMessageChannel(
            final SoeConnectionCache soeConnectionCache,
            final SoeProtocolHandler soeProtocolHandler,
            final SoeConnectionProvider soeConnectionProvider) {

        return new SoeInboundMessageChannel(
                soeConnectionCache,
                soeProtocolHandler,
                soeConnectionProvider
        );
    }

    @Bean
    @Inject
    @Qualifier("MainSendChannel")
    public UdpChannel getSendChannel(UdpReceiver receiver) {
        return receiver.getChannel();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }


}
