package com.ocdsoft.bacta.soe.protocol.network.handler;

import com.ocdsoft.bacta.engine.network.ConnectionState;
import com.ocdsoft.bacta.engine.network.handler.IncomingMessageHandler;
import com.ocdsoft.bacta.engine.network.handler.OutgoingMessageHandler;
import com.ocdsoft.bacta.engine.utils.BufferUtil;
import com.ocdsoft.bacta.soe.protocol.network.connection.ConnectionProvider;
import com.ocdsoft.bacta.soe.protocol.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.protocol.network.io.udp.ConnectionCache;
import com.ocdsoft.bacta.soe.protocol.network.message.UdpPacketType;
import com.ocdsoft.bacta.soe.protocol.util.SoeMessageUtil;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by kyle on 4/4/2017.
 */
@Slf4j
public final class ConnectionHandler implements IncomingMessageHandler<InetSocketAddress, ByteBuffer>, OutgoingMessageHandler<SoeUdpConnection, ByteBuffer> {

    private final ConnectionCache connectionCache;
    private final ProtocolHandler protocolHandler;
    private final ConnectionProvider connectionProvider;

    @Inject
    public ConnectionHandler(final ConnectionCache connectionCache,
                             final ProtocolHandler protocolHandler,
                             final ConnectionProvider connectionProvider) {

        this.connectionCache = connectionCache;
        this.protocolHandler = protocolHandler;
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void handleIncoming(final InetSocketAddress sender, final ByteBuffer buffer) {

        SoeUdpConnection connection = connectionCache.get(sender);

        byte type = buffer.get(1);
        if(type >= 0 && type <= 0x1E) {

            UdpPacketType packetType = UdpPacketType.values()[type];
            log.trace("Received {}", packetType);

            if (packetType == UdpPacketType.cUdpPacketConnect) {
                connection = connectionProvider.newInstance(sender);
                connection.setState(ConnectionState.ONLINE);
                connectionCache.put(sender, connection);
            }
        }

        if(connection != null) {
            protocolHandler.handleIncoming(connection, buffer);
        } else {
            log.debug("Unsolicited Message from " + sender + ": " + BufferUtil.bytesToHex(buffer));
        }
    }

    @Override
    public void handleOutgoing(final SoeUdpConnection connection, final ByteBuffer buffer) {


        log.trace("Sending message to {}:{} : {}", connection.getRemoteAddress().getAddress().getHostAddress(), connection.getRemoteAddress().getPort(), SoeMessageUtil.bytesToHex(buffer));
    }
}
