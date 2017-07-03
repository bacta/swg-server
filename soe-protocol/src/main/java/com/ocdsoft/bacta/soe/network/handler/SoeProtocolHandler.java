package com.ocdsoft.bacta.soe.network.handler;

import com.ocdsoft.bacta.soe.config.SoeNetworkConfiguration;
import com.ocdsoft.bacta.soe.network.SoeEncryption;
import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.network.dispatch.SoeMessageDispatcher;
import com.ocdsoft.bacta.soe.network.message.SoeMessageType;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by kyle on 4/4/2017.
 */
@Slf4j
public final class SoeProtocolHandler {

    private final SoeEncryption encryption;
    private final SoeMessageDispatcher soeMessageDispatcher;

    @Inject
    public SoeProtocolHandler(final SoeNetworkConfiguration networkConfiguration,
                              final SoeEncryption encryption,
                              final SoeMessageDispatcher soeMessageDispatcher) {
        this.encryption = encryption;
        this.soeMessageDispatcher = soeMessageDispatcher;
        encryption.setCompression(networkConfiguration.isCompression());
    }

//    @Override
//    public void sendMessage(UdpConnection sender, ByteBuffer buffer) {
//        UdpPacketType packetType = UdpPacketType.values()[buffer.get(1)];
//
//        if (packetType != UdpPacketType.cUdpPacketConnect && packetType != UdpPacketType.cUdpPacketConfirm) {
//            buffer = protocol.encode(soe.getConfiguration().getEncryptCode(), buffer, true);
//            protocol.appendCRC(soe.getConfiguration().getEncryptCode(), buffer, 2);
//            buffer.rewind();
//        }
//
//        return buffer;
//

    public void handleIncoming(SoeUdpConnection sender, ByteBuffer buffer) {
        SoeMessageType packetType = SoeMessageType.values()[buffer.get(1)];
        LOGGER.info("{}", packetType);
        ByteBuffer decodedBuffer;
        if (packetType != SoeMessageType.cUdpPacketConnect && packetType != SoeMessageType.cUdpPacketConfirm) {
            decodedBuffer = encryption.decode(sender.getConfiguration().getEncryptCode(), buffer.order(ByteOrder.LITTLE_ENDIAN));
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
