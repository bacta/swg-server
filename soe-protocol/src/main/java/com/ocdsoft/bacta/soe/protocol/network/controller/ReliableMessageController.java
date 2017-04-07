package com.ocdsoft.bacta.soe.protocol.network.controller;

import com.google.inject.Singleton;
import com.ocdsoft.bacta.soe.protocol.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.protocol.network.message.UdpPacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

@Singleton
@SoeController(handles = {
        UdpPacketType.cUdpPacketReliable1,
        UdpPacketType.cUdpPacketReliable2,
        UdpPacketType.cUdpPacketReliable3,
        UdpPacketType.cUdpPacketReliable4,
        UdpPacketType.cUdpPacketFragment1,
        UdpPacketType.cUdpPacketFragment2,
        UdpPacketType.cUdpPacketFragment3,
        UdpPacketType.cUdpPacketFragment4})
public class ReliableMessageController extends BaseSoeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReliableMessageController.class);

    @Override
    public void handleIncoming(byte zeroByte, UdpPacketType type, SoeUdpConnection connection, ByteBuffer buffer) {

        short sequenceNum = buffer.getShort();
        LOGGER.trace("{} Receiving Reliable Message Sequence {} {}", connection.getId(), sequenceNum, buffer.order());
        connection.sendAck(sequenceNum);

        if(type == UdpPacketType.cUdpPacketFragment1 ||
                type == UdpPacketType.cUdpPacketFragment2 ||
                type == UdpPacketType.cUdpPacketFragment3 ||
                type == UdpPacketType.cUdpPacketFragment4) {

            buffer = connection.addIncomingFragment(buffer);
        }

        if (buffer != null) {

            try {

                soeMessageDispatcher.dispatch(connection, buffer);

            } catch (Exception e) {
                LOGGER.error("Unable to handle ZeroEscape", e);
            }

        }
    }
}
