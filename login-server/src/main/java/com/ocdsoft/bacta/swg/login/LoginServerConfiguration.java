package com.ocdsoft.bacta.swg.login;

import com.codahale.metrics.MetricRegistry;
import com.ocdsoft.bacta.engine.network.udp.UdpEmitter;
import com.ocdsoft.bacta.engine.network.udp.UdpMetrics;
import com.ocdsoft.bacta.engine.network.udp.UdpReceiver;
import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnectionCache;
import com.ocdsoft.bacta.soe.network.handler.SoeUdpSendHandler;
import com.ocdsoft.bacta.engine.network.udp.netty.NettyUdpReceiver;
import com.ocdsoft.bacta.soe.network.handler.SoeInboundMessageChannel;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

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
    public UdpReceiver getLoginPublicReceiver(SoeInboundMessageChannel inboundMessageChannel, SoeUdpSendHandler sendHandler) {

        String metricsPrefix = "soe.server.connection.login.public";

        final UdpReceiver udpReceiver = new NettyUdpReceiver(
                loginServerProperties.getBindAddress(),
                loginServerProperties.getPublicBindPort(),
                new UdpMetrics(metricRegistry, metricsPrefix),
                inboundMessageChannel
        );

        SoeUdpConnectionCache connectionCache = inboundMessageChannel.getConnectionCache();
        UdpEmitter emitter = udpReceiver.start();
        sendHandler.start(metricsPrefix, connectionCache, emitter);

        return udpReceiver;
    }


    @Bean
    @Inject
    public UdpReceiver getLoginPrivateReceiver(SoeInboundMessageChannel inboundMessageChannel, SoeUdpSendHandler sendHandler) {

        String metricsPrefix = "soe.server.connection.login.private";

        final UdpReceiver udpReceiver = new NettyUdpReceiver(
                loginServerProperties.getBindAddress(),
                loginServerProperties.getPrivateBindPort(),
                new UdpMetrics(metricRegistry, metricsPrefix),
                inboundMessageChannel
        );

        SoeUdpConnectionCache connectionCache = inboundMessageChannel.getConnectionCache();
        UdpEmitter emitter = udpReceiver.start();
        sendHandler.start(metricsPrefix, connectionCache, emitter);

        return udpReceiver;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
