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

package io.bacta.soe.network.channel;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.Histogram;
import com.codahale.metrics.MetricRegistry;
import io.bacta.engine.buffer.BufferUtil;
import io.bacta.engine.network.connection.ConnectionState;
import io.bacta.engine.network.udp.UdpChannel;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.config.SoeNetworkConfiguration;
import io.bacta.soe.event.SoeChannelStartedEvent;
import io.bacta.soe.event.SoeChannelStoppedEvent;
import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.connection.SoeUdpConnectionCache;
import io.bacta.soe.network.dispatch.SoeControllerLoader;
import io.bacta.soe.network.dispatch.SoeMessageHandler;
import io.bacta.soe.network.message.SoeMessageType;
import io.bacta.soe.network.protocol.SoeProtocolHandler;
import io.bacta.soe.network.relay.GameNetworkMessageRelay;
import io.bacta.soe.util.SoeMessageUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Core inbound message channel for the SOE Protocol.  This channel is the entry point
 * that all messages enter for SOE protocol processing and dispatching
 *
 * @author Kyle Burkhardt
 * @since 1.0
 */
@Slf4j
@Getter
public class SoeMessageChannel implements Runnable {

    private final SoeNetworkConfiguration networkConfiguration;

    private final SoeProtocolHandler protocolHandler;
    private final SoeMessageHandler soeMessageHandler;
    private final SoeUdpConnectionCache connectionCache;
    private final GameNetworkMessageRelay gameNetworkMessageRelay;
    private UdpChannel udpChannel;
    private final ApplicationEventPublisher publisher;
    private final Thread sendThread;
    private Histogram sendQueueSizes;
    private final MetricRegistry metricRegistry;
    private final BroadcastService broadcastService;

    private String channelName = null;
    private InetAddress bindAddress = null;
    private int bindPort = 0;

    public SoeMessageChannel(final UdpChannel udpChannel,
                             final SoeProtocolHandler protocolHandler,
                             final SoeUdpConnectionCache connectionCache,
                             final SoeMessageHandler soeMessageHandler,
                             final SoeNetworkConfiguration networkConfiguration,
                             final GameNetworkMessageRelay gameNetworkMessageRelay,
                             final ApplicationEventPublisher publisher,
                             final BroadcastService broadcastService,
                             final MetricRegistry metricRegistry) {

        this.udpChannel = udpChannel;
        this.networkConfiguration = networkConfiguration;
        this.connectionCache = connectionCache;
        this.metricRegistry = metricRegistry;
        this.protocolHandler = protocolHandler;
        this.publisher = publisher;
        this.soeMessageHandler = soeMessageHandler;
        this.gameNetworkMessageRelay = gameNetworkMessageRelay;
        this.broadcastService = broadcastService;
        sendThread = new Thread(this);
    }

    /**
     * Receives {@link ByteBuffer} message with contextual information about the {@link io.bacta.engine.network.connection.Connection}
     * type based on the raw channel the message was received on.
     *
     * @param sender remote address of this message
     * @param message {@link ByteBuffer} representation of sent message
     */
    public void receiveMessage(final InetSocketAddress sender, final ByteBuffer message) {

        byte type = message.get(0);
        if(type == 0) {
            type = message.get(1);
        }

        if (type > 0 && type <= 0x1E) {

            SoeMessageType packetType = SoeMessageType.values()[type];

            SoeUdpConnection connection;

            if (packetType == SoeMessageType.cUdpPacketConnect) {
                connection = connectionCache.getNewConnection(sender);
            } else {
                connection = connectionCache.getIfPresent(sender);
            }

            if (connection != null) {

                onReceiveEncryptedMessage(connection, message);

                ByteBuffer decodedMessage = protocolHandler.processIncoming(connection, message, packetType);
                ByteBuffer processedMessage = connection.processIncomingProtocol(decodedMessage);

                // Incoming messages, post decoding
                onReceiveMessage(connection, processedMessage);

                if (processedMessage != null) {
                    soeMessageHandler.handleMessage(connection, processedMessage, gameNetworkMessageRelay);
                }

            } else {
                LOGGER.debug("Unsolicited Message from {}: {}", sender, BufferUtil.bytesToHex(message));
            }
        } else {
            LOGGER.error("Unhandled Message from {}: {}", sender, BufferUtil.bytesToHex(message));
        }
    }

    public InetSocketAddress getAddress() {
        return udpChannel.getAddress();
    }

    public void sendMessage(final SoeUdpConnection connection, final ByteBuffer message) {

        // Outgoing messages, pre-encoded
        onSendMessage(connection, message);

        ByteBuffer encodedMessage = protocolHandler.processOutgoing(connection, message);

        onSendEncryptedMessage(connection, encodedMessage);

        udpChannel.writeOutgoing(connection.getRemoteAddress(), encodedMessage);
    }

    private void onReceiveEncryptedMessage(SoeUdpConnection connection, ByteBuffer message) {
        if(LOGGER.isTraceEnabled()) {
            LOGGER.trace("Received encrypted message. Client: {}  Message: {}", connection.getId(), SoeMessageUtil.bytesToHex(message));
        }
    }

    private void onReceiveMessage(SoeUdpConnection connection, ByteBuffer message) {
        if(LOGGER.isTraceEnabled()) {
            LOGGER.trace("Decrypted message. Client: {}  Message: {}", connection.getId(), SoeMessageUtil.bytesToHex(message));
        }
    }

    private void onSendMessage(SoeUdpConnection connection, ByteBuffer message) {
        if(LOGGER.isTraceEnabled()) {
            LOGGER.trace("Sending message before encryption. Client: {}  Message: {}", connection.getId(), SoeMessageUtil.bytesToHex(message));
        }
    }

    private void onSendEncryptedMessage(SoeUdpConnection connection, ByteBuffer message) {
        if(LOGGER.isTraceEnabled()) {
            LOGGER.trace("Sending message after encryption. Client: {}  Message: {}", connection.getId(), SoeMessageUtil.bytesToHex(message));
        }
    }

    public void broadcast(GameNetworkMessage message) {
        connectionCache.broadcast(message);
    }

    @EventListener
    public void handleApplicationStarted(ApplicationStartedEvent startedEvent) {
        if(channelName == null || bindAddress == null) {
            throw new ChannelNotConfiguredException("channelName or bindAddress not configured");
        }

        soeMessageHandler.setControllers(SoeControllerLoader.loadControllers(startedEvent.getApplicationContext(), connectionCache, soeMessageHandler));
        broadcastService.setChannel(this);

        if (!sendThread.isAlive()) {
            sendThread.setName(channelName + "-Send");
            sendQueueSizes = metricRegistry.histogram(channelName + ".outgoing-queue");
            metricRegistry.register(channelName, (Gauge<Long>) connectionCache::size);
            this.udpChannel.start(channelName, bindAddress, bindPort, this::receiveMessage);
            sendThread.start();
            publisher.publishEvent(new SoeChannelStartedEvent());
        }
    }

    @EventListener
    public void handleShutdown(ContextClosedEvent closedEvent) {
        sendThread.interrupt();
        this.udpChannel.stop();
        publisher.publishEvent(new SoeChannelStoppedEvent());
    }

    @Override
    public void run() {
        long nextIteration = 0;
        long idleTimestamp = System.currentTimeMillis() - (1000 * 60 * 2);

        boolean run = true;
        while (run) {
            try {
                long currentTime = System.currentTimeMillis();

                if (nextIteration > currentTime) {
                    Thread.sleep(nextIteration - currentTime);
                }

                nextIteration = currentTime + networkConfiguration.getNetworkThreadSleepTimeMs();

                if((currentTime - (1000 * 60 * 2)) > idleTimestamp) {
                    idleTimestamp = currentTime - (1000 * 60 * 2);
                }

                sendPendingMessages(idleTimestamp);

            } catch (InterruptedException e) {
                run = false;
                LOGGER.info("Shutting down", e);
                Thread.currentThread().interrupt();
            }
        }
    }

    private void sendPendingMessages(long idleTimeStamp) {

        try {
            final List<SoeUdpConnection> deadClients = new ArrayList<>();

            for (SoeUdpConnection connection : connectionCache.getAsCollection()) {

                if (connection == null) {
                    continue;
                }

                if(connection.getLastRemoteActivity() < idleTimeStamp || connection.getLastActivity() < idleTimeStamp) {
                    connection.setConnectionState(ConnectionState.DISCONNECTED);
                }

                if (connection.getConnectionState() == ConnectionState.DISCONNECTED) {
                    deadClients.add(connection);
                }

                final List<ByteBuffer> messages = connection.getPendingMessages();
                if (!messages.isEmpty()) {
                    sendQueueSizes.update(messages.size());
                }

                for (final ByteBuffer message : messages) {
                    sendMessage(connection, message);
                }
            }

            for (SoeUdpConnection connection : deadClients) {
                connectionCache.invalidate(connection.getRemoteAddress());
            }

        } catch (Exception e) {
            LOGGER.error("Unknown", e);
        }
    }

    public void configure(String channelName, InetAddress bindAddress, int bindPort) {
        this.channelName = channelName;
        this.bindAddress = bindAddress;
        this.bindPort = bindPort;
    }
}
