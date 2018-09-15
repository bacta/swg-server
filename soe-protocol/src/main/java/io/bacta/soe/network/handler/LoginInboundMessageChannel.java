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

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.engine.network.channel.InboundMessageChannel;
import io.bacta.engine.network.connection.ConnectionState;
import io.bacta.soe.network.connection.*;
import io.bacta.soe.network.dispatch.SoeMessageDispatcher;
import io.bacta.soe.network.message.SoeMessageType;
import io.bacta.soe.util.SoeMessageUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Core inbound message channel for the SOE Protocol.  This channel is the entry point
 * that all messages enter for SOE protocol processing and dispatching
 *
 * @author Kyle Burkhardt
 * @since 1.0
 */
@Slf4j
@Component
@Scope("prototype")
@Getter
public class LoginInboundMessageChannel implements InboundMessageChannel {

    private final SoeConnectionCache connectionCache;
    private final SoeProtocolHandler protocolHandler;
    private final SoeConnectionFactory connectionProvider;
    private final SoeMessageDispatcher soeMessageDispatcher;

    @Inject
    public LoginInboundMessageChannel(final SoeConnectionCache connectionCache,
                                      final SoeProtocolHandler protocolHandler,
                                      final SoeConnectionFactory connectionProvider,
                                      final SoeMessageDispatcher soeMessageDispatcher) {

        this.connectionCache = connectionCache;
        this.protocolHandler = protocolHandler;
        this.connectionProvider = connectionProvider;
        this.soeMessageDispatcher = soeMessageDispatcher;
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

        SoeConnection connection = connectionCache.get(sender);
        SoeMessageType packetType = SoeMessageType.values()[type];

        if (type <= 0x1E) {

            if (packetType == SoeMessageType.cUdpPacketConnect) {
                connection = connectionProvider.newInstance(sender);
                connection.setState(ConnectionState.ONLINE);
                connectionCache.put(sender, connection);
            }

            if (connection != null) {
                LOGGER.trace("Received raw message from {} {}", sender, SoeMessageUtil.bytesToHex(message));

                SoeUdpConnection soeUdpConnection = connection.getSoeUdpConnection();
                SoeIncomingMessageProcessor incomingMessageProcessor = soeUdpConnection.getIncomingMessageProcessor();

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
}
