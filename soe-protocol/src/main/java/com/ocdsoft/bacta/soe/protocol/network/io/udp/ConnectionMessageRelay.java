package com.ocdsoft.bacta.soe.protocol.network.io.udp;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.ocdsoft.bacta.engine.io.network.ConnectionState;
import com.ocdsoft.bacta.engine.io.network.udp.UdpSendChannel;
import com.ocdsoft.bacta.soe.protocol.event.DisconnectEvent;
import com.ocdsoft.bacta.soe.protocol.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.protocol.service.PublisherService;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by kyle on 4/4/2017.
 */
@Singleton
@Slf4j
public class ConnectionMessageRelay implements Runnable {

    private final SoeNetworkConfiguration networkConfiguration;
    private final ConnectionCache connectionCache;
    private final PublisherService publisherService;
    private final UdpSendChannel sendChannel;

    private final Timer sendTimer;
    private final Histogram sendQueueSizes;

    @Inject
    public ConnectionMessageRelay(final SoeNetworkConfiguration networkConfiguration,
                                  final ConnectionCache connectionCache,
                                  final MetricRegistry metrics,
                                  final PublisherService publisherService,
                                  final UdpSendChannel sendChannel) {

        this.networkConfiguration = networkConfiguration;
        this.connectionCache = connectionCache;
        this.publisherService = publisherService;
        this.sendChannel = sendChannel;

        sendTimer = metrics.timer(MetricRegistry.name(ConnectionMessageRelay.class, "send-timer"));
        sendQueueSizes = metrics.histogram(MetricRegistry.name(ConnectionMessageRelay.class, "outgoing-queue"));
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

                Timer.Context context = sendTimer.time();

                try {

                    nextIteration = currentTime + networkConfiguration.getNetworkThreadSleepTimeMs();

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
                                log.info("Client disconnected: {}  Connection: {}  Reason: {}", connection.getRemoteAddress(), connection.getId(), connection.getTerminateReason().getReason());
                                log.info("Clients still connected: {}", connectionCache.getConnectionCount());
                            }
                        }
                    }

                } catch (Exception e) {
                    log.error("Unknown", e);
                }
                context.stop();
            }
        } catch (InterruptedException e) {
            log.warn("Send thread interrupted", e);
        }
    }

}
