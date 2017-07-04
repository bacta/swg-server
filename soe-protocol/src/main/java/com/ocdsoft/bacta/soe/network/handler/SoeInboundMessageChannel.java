package com.ocdsoft.bacta.soe.network.handler;

import com.ocdsoft.bacta.engine.buffer.BufferUtil;
import com.ocdsoft.bacta.engine.network.ConnectionState;
import com.ocdsoft.bacta.engine.network.channel.InboundMessageChannel;
import com.ocdsoft.bacta.soe.network.connection.SoeConnectionProvider;
import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnectionCache;
import com.ocdsoft.bacta.soe.network.message.SoeMessageType;
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
