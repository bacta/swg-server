package com.ocdsoft.bacta.soe.protocol.network.io.udp;


import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.annotation.Timed;
import com.ocdsoft.bacta.engine.io.network.ConnectionState;
import com.ocdsoft.bacta.soe.protocol.SharedNetworkConfiguration;
import com.ocdsoft.bacta.soe.protocol.event.DisconnectEvent;
import com.ocdsoft.bacta.soe.protocol.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.protocol.service.PublisherService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by kyle on 4/4/2017.
 */
@Slf4j
public class ConnectionMessageRelay implements Runnable {

    private final SharedNetworkConfiguration networkConfiguration;
    private final ConnectionCache connectionCache;
    private final PublisherService publisherService;
    private final UdpSendChannel sendChannel;

    private final Histogram sendQueueSizes;

    @Inject
    public ConnectionMessageRelay(final SharedNetworkConfiguration networkConfiguration,
                                  final ConnectionCache connectionCache,
                                  final PublisherService publisherService,
                                  final MetricRegistry metricRegistry,
                                  final UdpSendChannel sendChannel) {

        this.networkConfiguration = networkConfiguration;
        this.connectionCache = connectionCache;
        this.publisherService = publisherService;
        this.sendChannel = sendChannel;

        sendQueueSizes = metricRegistry.histogram(MetricRegistry.name(ConnectionMessageRelay.class, "outgoing-queue"));
    }

    @Override
    public void run() {
        long nextIteration = 0;

        try {

            while(!sendChannel.isOpen()) {
                Thread.sleep(100);
            }

            while (true) {

                long currentTime = System.currentTimeMillis();

                if (nextIteration > currentTime) {
                    Thread.sleep(nextIteration - currentTime);
                }

                try {

                    nextIteration = currentTime + networkConfiguration.getNetworkThreadSleepTimeMs();
                    doStuff();

                } catch (Exception e) {
                    LOGGER.error("Unknown", e);
                }
            }
        } catch (InterruptedException e) {
            LOGGER.warn("Send thread interrupted", e);
        }
    }

    @Timed
    private void doStuff() {

        final Set<InetSocketAddress> connectionList = connectionCache.keySet();
        final List<InetSocketAddress> deadClients = new ArrayList<>();

        for (InetSocketAddress inetSocketAddress : connectionList) {
            final SoeUdpConnection connection = connectionCache.get(inetSocketAddress);

            if (connection == null) {
                continue;
            }

            if (connection.getState() == ConnectionState.DISCONNECTED) {
                deadClients.add(inetSocketAddress);
            }

            final List<ByteBuffer> messages = connection.getPendingMessages();
            if(messages.size() > 0) {
                sendQueueSizes.update(messages.size());
            }

            for (final ByteBuffer message : messages) {
                sendChannel.send(connection, message);
            }
        }

        for (InetSocketAddress inetSocketAddress : deadClients) {
            final SoeUdpConnection connection = connectionCache.remove(inetSocketAddress);
            if(connection != null) {
                publisherService.onEvent(new DisconnectEvent(connection));

                if (networkConfiguration.isReportUdpDisconnects()) {
                    LOGGER.info("Client disconnected: {}  Connection: {}  Reason: {}", connection.getRemoteAddress(), connection.getId(), connection.getTerminateReason().getReason());
                    LOGGER.info("Clients still connected: {}", connectionCache.getConnectionCount());
                }
            }
        }
    }

}
