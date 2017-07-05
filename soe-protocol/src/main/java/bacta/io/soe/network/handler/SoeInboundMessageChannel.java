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

package bacta.io.soe.network.handler;

import bacta.io.buffer.BufferUtil;
import bacta.io.network.ConnectionState;
import bacta.io.network.channel.InboundMessageChannel;
import bacta.io.soe.network.connection.SoeConnectionProvider;
import bacta.io.soe.network.connection.SoeUdpConnection;
import bacta.io.soe.network.connection.SoeUdpConnectionCache;
import bacta.io.soe.network.message.SoeMessageType;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by kyle on 6/28/2017.
 */
@Slf4j
@Component
@Scope("prototype")
public class SoeInboundMessageChannel implements InboundMessageChannel {

    @Getter
    private final SoeUdpConnectionCache connectionCache;
    private final SoeProtocolHandler protocolHandler;
    private final SoeConnectionProvider connectionProvider;

    @Inject
    public SoeInboundMessageChannel(final SoeUdpConnectionCache connectionCache,
                                    final SoeProtocolHandler protocolHandler,
                                    final SoeConnectionProvider connectionProvider) {

        this.connectionCache = connectionCache;
        this.protocolHandler = protocolHandler;
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void receiveMessage(InetSocketAddress sender, ByteBuffer message) {

        SoeUdpConnection connection = connectionCache.get(sender);

        byte type = message.get(1);
        if(type >= 0 && type <= 0x1E) {

            SoeMessageType packetType = SoeMessageType.values()[type];
            LOGGER.trace("Received {}", packetType);

            if (packetType == SoeMessageType.cUdpPacketConnect) {
                connection = connectionProvider.newInstance(sender);
                connection.setState(ConnectionState.ONLINE);
                connectionCache.put(sender, connection);
            }
        }

        if(connection != null) {
            protocolHandler.handleIncoming(connection, message);
        } else {
            LOGGER.debug("Unsolicited Message from " + sender + ": " + BufferUtil.bytesToHex(message));
        }
    }
}
