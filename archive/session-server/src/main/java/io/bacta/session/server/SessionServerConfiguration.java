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

package io.bacta.session.server;

import com.codahale.metrics.MetricRegistry;
import io.bacta.engine.network.udp.UdpEmitter;
import io.bacta.engine.network.udp.UdpMetrics;
import io.bacta.engine.network.udp.UdpReceiver;
import io.bacta.engine.network.udp.netty.NettyUdpReceiver;
import io.bacta.soe.network.connection.SoeConnectionCache;
import io.bacta.soe.network.handler.SoeInboundMessageChannel;
import io.bacta.soe.network.handler.SoeProtocolHandler;
import io.bacta.soe.network.handler.SoeUdpSendHandler;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

/**
 * Created by crush on 7/6/2017.
 */
@Configuration
@ConfigurationProperties
public class SessionServerConfiguration {
    private final SessionServerProperties sessionServerProperties;
    private final MetricRegistry metricRegistry;

    @Inject
    public SessionServerConfiguration(final SessionServerProperties sessionServerProperties,
                                      final MetricRegistry metricRegistry) {
        this.sessionServerProperties = sessionServerProperties;
        this.metricRegistry = metricRegistry;
    }

    @Bean
    @Inject
    public UdpReceiver getSessionReceiver(SoeInboundMessageChannel inboundMessageChannel, SoeUdpSendHandler sendHandler) {

        String metricsPrefix = "io.bacta.session.server";

        final UdpReceiver udpReceiver = new NettyUdpReceiver(
                sessionServerProperties.getBindAddress(),
                sessionServerProperties.getBindPort(),
                new UdpMetrics(metricRegistry, metricsPrefix),
                inboundMessageChannel
        );

        SoeConnectionCache connectionCache = inboundMessageChannel.getConnectionCache();
        UdpEmitter emitter = udpReceiver.start();
        SoeProtocolHandler protocolHandler = inboundMessageChannel.getProtocolHandler();
        sendHandler.start(metricsPrefix, connectionCache, protocolHandler, emitter);

        return udpReceiver;
    }
}
