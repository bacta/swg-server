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

package io.bacta.soe.network.handler;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import io.bacta.engine.network.connection.ConnectionState;
import io.bacta.engine.network.udp.UdpChannel;
import io.bacta.soe.config.SoeNetworkConfiguration;
import io.bacta.soe.event.DisconnectEvent;
import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.soe.network.connection.SoeConnectionCache;
import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.service.PublisherService;
import io.bacta.soe.util.SoeMessageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by kyle on 7/3/2017.
 */

@Slf4j
@Component
@Scope("prototype")
class DefaultSoeSendHandler implements SoeSendHandler, Runnable {

    private final SoeNetworkConfiguration networkConfiguration;
    private final PublisherService publisherService;
    private final MetricRegistry metricRegistry;

    private UdpChannel udpChannel;
    private SoeConnectionCache connectionCache;
    private SoeProtocolHandler protocolHandler;

    private Histogram sendQueueSizes;
    private Gauge<Integer> sizeGauge;

    private final Thread sendThread;

    @Inject
    public DefaultSoeSendHandler(final SoeNetworkConfiguration networkConfiguration,
                                 final PublisherService publisherService,
                                 final MetricRegistry metricRegistry) {

        this.networkConfiguration = networkConfiguration;
        this.publisherService = publisherService;
        this.metricRegistry = metricRegistry;

        sendThread = new Thread(this);
    }

    @Override
    public void start(final String metricPrefix, final SoeConnectionCache connectionCache, final SoeProtocolHandler protocolHandler, final UdpChannel udpChannel) {

        if (!sendThread.isAlive()) {
            sendThread.setName(metricPrefix + "-Send");
            sendQueueSizes = metricRegistry.histogram(metricPrefix + ".outgoing-queue");
            metricRegistry.register(metricPrefix, (Gauge<Integer>) () -> connectionCache.getConnectionCount());
            this.connectionCache = connectionCache;
            this.protocolHandler = protocolHandler;
            this.udpChannel = udpChannel;
            sendThread.start();
        }
    }

    public void stop() {
        sendThread.interrupt();
    }

    @Override
    public void run() {
        long nextIteration = 0;

        try {

            while (true) {

                long currentTime = System.currentTimeMillis();

                if (nextIteration > currentTime) {
                    Thread.sleep(nextIteration - currentTime);
                }

                try {

                    nextIteration = currentTime + networkConfiguration.getNetworkThreadSleepTimeMs();
                    sendPendingMessages();

                } catch (Exception e) {
                    LOGGER.error("Unknown", e);
                }
            }
        } catch (InterruptedException e) {
            LOGGER.info("Shutting down");
        }
    }

    @Override
    public void sendMessage(final SoeConnection connectionProxy, final ByteBuffer message) {
        final SoeUdpConnection connection = connectionProxy.getSoeUdpConnection();
        ByteBuffer encodedMessage = protocolHandler.processOutgoing(connectionProxy, message);
        udpChannel.writeOutgoing(connection.getRemoteAddress(), encodedMessage);
        if(LOGGER.isTraceEnabled()) {
            LOGGER.trace("Sending to {} {}", connection.getRemoteAddress(), SoeMessageUtil.bytesToHex(encodedMessage));
        }
    }


    private void sendPendingMessages() {

        final Set<InetSocketAddress> connectionList = connectionCache.keySet();
        final List<InetSocketAddress> deadClients = new ArrayList<>();

        for (InetSocketAddress inetSocketAddress : connectionList) {
            final SoeConnection connectionProxy = connectionCache.get(inetSocketAddress);

            if (connectionProxy == null) {
                continue;
            }

            final SoeUdpConnection connection = connectionProxy.getSoeUdpConnection();

            if (connection == null) {
                continue;
            }

            if (connection.getConnectionState() == ConnectionState.DISCONNECTED) {
                deadClients.add(inetSocketAddress);
            }

            final List<ByteBuffer> messages = connection.getPendingMessages();
            if(messages.isEmpty()) {
                sendQueueSizes.update(messages.size());
            }

            for (final ByteBuffer message : messages) {
                sendMessage(connectionProxy, message);
            }
        }

        for (InetSocketAddress inetSocketAddress : deadClients) {
            final SoeConnection connectionProxy = connectionCache.remove(inetSocketAddress);
            final SoeUdpConnection connection = connectionProxy.getSoeUdpConnection();

            if(connection != null) {
                publisherService.onEvent(new DisconnectEvent(connectionProxy));

                if (networkConfiguration.isReportUdpDisconnects()) {
                    LOGGER.info("Client disconnected: {}  Connection: {}  Reason: {}", connection.getRemoteAddress(), connection.getId(), connection.getTerminateReason().getReason());
                    LOGGER.info("Clients still connected: {}", connectionCache.getConnectionCount());
                }
            }
        }
    }
}
