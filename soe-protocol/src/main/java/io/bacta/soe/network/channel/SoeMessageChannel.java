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
import io.bacta.engine.network.channel.InboundMessageChannel;
import io.bacta.engine.network.connection.ConnectionState;
import io.bacta.engine.network.udp.UdpChannel;
import io.bacta.soe.config.SoeNetworkConfiguration;
import io.bacta.soe.network.connection.SoeIncomingMessageProcessor;
import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.connection.SoeUdpConnectionCache;
import io.bacta.soe.network.dispatch.SoeMessageDispatcher;
import io.bacta.soe.network.message.SoeMessageType;
import io.bacta.soe.network.protocol.SoeProtocolHandler;
import io.bacta.soe.util.SoeMessageUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Core inbound message channel for the SOE Protocol.  This channel is the entry point
 * that all messages enter for SOE protocol processing and dispatching
 *
 * @author Kyle Burkhardt
 * @since 1.0
 */
@Slf4j
@Component
@Getter
public class SoeMessageChannel implements InboundMessageChannel, SoeSendHandler, Runnable {

    private final SoeNetworkConfiguration networkConfiguration;

    private final SoeProtocolHandler protocolHandler;
    private final SoeMessageDispatcher soeMessageDispatcher;
    private final SoeUdpConnectionCache connectionCache;
    private UdpChannel udpChannel;

    private final Thread sendThread;
    private Histogram sendQueueSizes;
    private final MetricRegistry metricRegistry;

    @Inject
    public SoeMessageChannel(final SoeProtocolHandler protocolHandler,
                             final SoeUdpConnectionCache connectionCache,
                             final SoeMessageDispatcher soeMessageDispatcher,
                             final SoeNetworkConfiguration networkConfiguration,
                             final ApplicationEventPublisher publisher,
                             final MetricRegistry metricRegistry) {

        this.networkConfiguration = networkConfiguration;
        this.connectionCache = connectionCache;
        this.metricRegistry = metricRegistry;
        this.protocolHandler = protocolHandler;
        this.soeMessageDispatcher = soeMessageDispatcher;
        sendThread = new Thread(this);
    }

    /**
     * Receives {@link ByteBuffer} message with contextual information about the {@link io.bacta.engine.network.connection.Connection}
     * type based on the raw channel the message was received on.
     *
     * @param sender remote address of this message
     * @param message {@link ByteBuffer} representation of sent message
     */
    @Override
    public void receiveMessage(final InetSocketAddress sender, final ByteBuffer message) {

        byte type = message.get(1);

        SoeUdpConnection connection = connectionCache.getIfPresent(sender);
        SoeMessageType packetType = SoeMessageType.values()[type];

        if (type <= 0x1E) {

            if (packetType == SoeMessageType.cUdpPacketConnect) {
                connection = connectionCache.getNewConnection(sender);
            }

            if (connection != null) {
                LOGGER.trace("Received raw message from {} {}", sender, SoeMessageUtil.bytesToHex(message));

                SoeIncomingMessageProcessor incomingMessageProcessor = connection.getIncomingMessageProcessor();

                ByteBuffer decodedMessage = protocolHandler.processIncoming(connection, message);
                ByteBuffer processedMessage = incomingMessageProcessor.processIncomingProtocol(decodedMessage);

                if (processedMessage != null) {
                    soeMessageDispatcher.dispatch(connection, processedMessage);
                }

            } else {
                LOGGER.debug("Unsolicited Message from {}: {}", sender, BufferUtil.bytesToHex(message));
            }
        }
    }

    @Override
    public void sendMessage(final SoeUdpConnection connection, final ByteBuffer message) {
        ByteBuffer encodedMessage = protocolHandler.processOutgoing(connection, message);
        udpChannel.writeOutgoing(connection.getRemoteAddress(), encodedMessage);
        if(LOGGER.isTraceEnabled()) {
            LOGGER.trace("Sending to {} {}", connection.getRemoteAddress(), SoeMessageUtil.bytesToHex(encodedMessage));
        }
    }

    @Override
    public void start(final String metricPrefix, final UdpChannel udpChannel) {

        if (!sendThread.isAlive()) {
            sendThread.setName(metricPrefix + "-Send");
            sendQueueSizes = metricRegistry.histogram(metricPrefix + ".outgoing-queue");
            metricRegistry.register(metricPrefix, (Gauge<Long>) connectionCache::size);
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

    private void sendPendingMessages() {

        final Set<InetSocketAddress> connectionList = connectionCache.asMap().keySet();
        final List<InetSocketAddress> deadClients = new ArrayList<>();

        for (InetSocketAddress inetSocketAddress : connectionList) {
            final SoeUdpConnection connection = connectionCache.getIfPresent(inetSocketAddress);

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
                sendMessage(connection, message);
            }
        }

        for (InetSocketAddress inetSocketAddress : deadClients) {
            connectionCache.invalidate(inetSocketAddress);
        }
    }
}
