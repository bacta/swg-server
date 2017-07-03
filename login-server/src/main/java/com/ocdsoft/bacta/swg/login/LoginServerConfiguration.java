package com.ocdsoft.bacta.swg.login;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.ocdsoft.bacta.engine.conf.BactaConfiguration;
import com.ocdsoft.bacta.engine.conf.NetworkConfigurationImpl;
import com.ocdsoft.bacta.engine.network.channel.InboundMessageChannel;
import com.ocdsoft.bacta.engine.network.udp.UdpChannel;
import com.ocdsoft.bacta.engine.network.udp.UdpReceiver;
import com.ocdsoft.bacta.engine.network.udp.UdpReceiverMetrics;
import com.ocdsoft.bacta.engine.network.udp.netty.NettyUdpReceiver;
import com.ocdsoft.bacta.soe.config.SoeNetworkConfiguration;
import com.ocdsoft.bacta.soe.config.SoeNetworkConfigurationImpl;
import com.ocdsoft.bacta.soe.network.connection.SoeConnectionCache;
import com.ocdsoft.bacta.soe.network.handler.SoeInboundMessageChannel;
import com.ocdsoft.bacta.soe.network.udp.ConnectionMessageRelay;
import com.ocdsoft.bacta.soe.service.PublisherService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.net.UnknownHostException;

/**
 * Created by kyle on 4/12/2016.
 */

@Configuration
@ConfigurationProperties
public class LoginServerConfiguration implements ApplicationContextAware {

    private ApplicationContext applicationContext;
    private final MetricRegistry metricRegistry;
    private final LoginServerProperties loginServerProperties;

    @Inject
    public LoginServerConfiguration(final MetricRegistry metricRegistry,
                                    final LoginServerProperties loginServerProperties) {
        this.metricRegistry = metricRegistry;
        this.loginServerProperties = loginServerProperties;
    }

    @Bean
    @Inject
    public UdpReceiver getLoginReceiver(SoeInboundMessageChannel inboundMessageChannel) {

        final UdpReceiver udpReceiver = new NettyUdpReceiver(
                loginServerProperties.getBindAddress(),
                loginServerProperties.getBindPort(),
                new UdpReceiverMetrics(metricRegistry, "soe.server.connection.login"),
                inboundMessageChannel
        );

        udpReceiver.start();
        return udpReceiver;
    }

    @Bean
    @Inject
    public ConnectionMessageRelay getSendRelay(final SoeNetworkConfiguration networkConfiguration,
                                               final SoeConnectionCache connectionCache,
                                               final PublisherService publisherService,
                                               final MetricRegistry metricRegistry,
                                               final UdpChannel sendChannel) {

        ConnectionMessageRelay relay = new ConnectionMessageRelay (
                networkConfiguration,
                connectionCache,
                publisherService,
                metricRegistry,
                sendChannel
        );
        Thread thread = new Thread(relay);
        thread.setName("LoginSend");
        thread.start();

        return relay;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
