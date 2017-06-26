package com.ocdsoft.bacta.soe.protocol.network.handler;

import com.ocdsoft.bacta.engine.io.network.channel.InboundMessageChannel;
import com.ocdsoft.bacta.engine.io.network.channel.OutboundMessageChannel;
import com.ocdsoft.bacta.engine.io.network.udp.UdpConnection;
import com.ocdsoft.bacta.soe.protocol.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.protocol.network.dispatch.SoeMessageDispatcher;
import com.ocdsoft.bacta.soe.protocol.SharedNetworkConfiguration;
import com.ocdsoft.bacta.soe.protocol.network.message.UdpPacketType;
import com.ocdsoft.bacta.soe.protocol.network.protocol.SoeProtocol;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by kyle on 4/4/2017.
 */
@Slf4j
public final class ProtocolHandler implements InboundMessageChannel<SoeUdpConnection, ByteBuffer>, OutboundMessageChannel<SoeUdpConnection, ByteBuffer> {

    private final SoeProtocol protocol;
    private final SoeMessageDispatcher soeMessageDispatcher;

    @Inject
    public ProtocolHandler(final SharedNetworkConfiguration networkConfiguration,
                           final SoeProtocol protocol,
                           final SoeMessageDispatcher soeMessageDispatcher) {
        this.protocol = protocol;
        this.soeMessageDispatcher = soeMessageDispatcher;
        protocol.setCompression(networkConfiguration.isCompression());
    }

    @Override
    public void sendMessage(UdpConnection sender, ByteBuffer buffer) {
        UdpPacketType packetType = UdpPacketType.values()[buffer.get(1)];

        if (packetType != UdpPacketType.cUdpPacketConnect && packetType != UdpPacketType.cUdpPacketConfirm) {
            buffer = protocol.encode(connection.getConfiguration().getEncryptCode(), buffer, true);
            protocol.appendCRC(connection.getConfiguration().getEncryptCode(), buffer, 2);
            buffer.rewind();
        }

        return buffer;
    }

    @Override
    public void receiveMessage(InetSocketAddress sender, ByteBuffer buffer) {
        UdpPacketType packetType = UdpPacketType.values()[buffer.get(1)];
        ByteBuffer decodedBuffer;
        if (packetType != UdpPacketType.cUdpPacketConnect && packetType != UdpPacketType.cUdpPacketConfirm) {
            decodedBuffer = protocol.decode(sender.getConfiguration().getEncryptCode(), buffer.order(ByteOrder.LITTLE_ENDIAN));
        } else {
            decodedBuffer = buffer;
        }

        if(decodedBuffer != null) {
            sender.increaseProtocolMessageReceived();
            soeMessageDispatcher.dispatch(sender, decodedBuffer);
        } else {
            LOGGER.warn("Unhandled message {}}", packetType);
        }
    }
}
