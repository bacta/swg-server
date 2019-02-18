/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.bacta.login.server;

import com.codahale.metrics.MetricRegistry;
import io.bacta.engine.network.channel.InboundMessageChannel;
import io.bacta.engine.network.udp.*;
import io.bacta.engine.network.udp.netty.NettyUdpReceiver;
import io.bacta.soe.network.connection.ClientConnection;
import io.bacta.soe.network.connection.SoeConnectionCache;
import io.bacta.soe.network.channel.SoeInboundMessageChannel;
import io.bacta.soe.network.channel.SoeProtocolHandler;
import io.bacta.soe.network.channel.SoeUdpSendHandler;
import io.bacta.soe.network.udp.SoeUdpChannelBuilder;
import io.bacta.soe.network.udp.SoeUdpTransceiverGroup;
import io.bacta.soe.service.InternalMessageService;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.net.InetAddress;

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

    @Inject
    @Bean
    public SoeUdpTransceiverGroup startReceiverGroup(SoeUdpTransceiverGroup transceiverGroup, InboundMessageChannel inboundMessageChannel) {

        transceiverGroup.registerChannel(SoeUdpChannelBuilder.newBuilder()
                .withMetricsRegistry(metricRegistry)
                .withMetricsPrefix("io.bacta.login.port.client")
                .withAddress(InetAddress.getLoopbackAddress())
                .withPort(1234)
                .withConnection(ClientConnection.class)
                .usingInboundChannel(inboundMessageChannel)
                .build()
        );

        return transceiverGroup;
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

        SoeConnectionCache connectionCache = inboundMessageChannel.getConnectionCache();
        UdpEmitter emitter = udpReceiver.start();
        SoeProtocolHandler protocolHandler = inboundMessageChannel.getProtocolHandler();
        sendHandler.start(metricsPrefix, connectionCache, protocolHandler, emitter);

        return udpReceiver;
    }


    @Bean
    @Inject
    public UdpReceiver getLoginPrivateReceiver(SoeInboundMessageChannel inboundMessageChannel,
                                               SoeUdpSendHandler sendHandler,
                                               InternalMessageService internalMessageService) {

        String metricsPrefix = "soe.server.connection.login.private";

        final UdpReceiver udpReceiver = new NettyUdpReceiver(
                loginServerProperties.getBindAddress(),
                loginServerProperties.getPrivateBindPort(),
                new UdpMetrics(metricRegistry, metricsPrefix),
                inboundMessageChannel
        );

        SoeConnectionCache connectionCache = inboundMessageChannel.getConnectionCache();
        UdpEmitter emitter = udpReceiver.start();
        SoeProtocolHandler protocolHandler = inboundMessageChannel.getProtocolHandler();
        sendHandler.start(metricsPrefix, connectionCache, protocolHandler, emitter);
        internalMessageService.setConnectionObjects(connectionCache, inboundMessageChannel.getConnectionProvider());

        return udpReceiver;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
