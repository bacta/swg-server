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

package io.bacta.galaxy.server;

import com.codahale.metrics.MetricRegistry;
import io.bacta.network.udp.UdpEmitter;
import io.bacta.network.udp.UdpMetrics;
import io.bacta.network.udp.UdpReceiver;
import io.bacta.network.udp.netty.NettyUdpReceiver;
import io.bacta.soe.network.connection.SoeUdpConnectionCache;
import io.bacta.soe.network.handler.SoeInboundMessageChannel;
import io.bacta.soe.network.handler.SoeProtocolHandler;
import io.bacta.soe.network.handler.SoeUdpSendHandler;
import io.bacta.soe.service.InternalMessageService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

/**
 * Created by kyle on 4/12/2016.
 */

@Configuration
@ConfigurationProperties
public class GalaxyServerConfiguration {

    private final MetricRegistry metricRegistry;
    private final GalaxyServerProperties galaxyServerProperties;

    @Inject
    public GalaxyServerConfiguration(final MetricRegistry metricRegistry,
                                     final GalaxyServerProperties galaxyServerProperties) {
        this.metricRegistry = metricRegistry;
        this.galaxyServerProperties = galaxyServerProperties;
    }

    @Bean
    @Inject
    public UdpReceiver getGalaxyPrivateReceiver(SoeInboundMessageChannel inboundMessageChannel,
                                               SoeUdpSendHandler sendHandler,
                                               InternalMessageService internalMessageService) {

        String metricsPrefix = "io.bacta.galaxy.server";

        final UdpReceiver udpReceiver = new NettyUdpReceiver(
                galaxyServerProperties.getBindAddress(),
                galaxyServerProperties.getBindPort(),
                new UdpMetrics(metricRegistry, metricsPrefix),
                inboundMessageChannel
        );

        SoeUdpConnectionCache connectionCache = inboundMessageChannel.getConnectionCache();
        UdpEmitter emitter = udpReceiver.start();
        SoeProtocolHandler protocolHandler = inboundMessageChannel.getProtocolHandler();
        sendHandler.start(metricsPrefix, connectionCache, protocolHandler, emitter);
        internalMessageService.setConnectionCache(connectionCache);
        internalMessageService.setConnectionProvider(inboundMessageChannel.getConnectionProvider());

        return udpReceiver;
    }
}
