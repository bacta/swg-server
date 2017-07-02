package com.ocdsoft.bacta.soe.network.controller;

import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.network.message.SoeMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

@SoeController(handles = {
        SoeMessageType.cUdpPacketReliable1,
        SoeMessageType.cUdpPacketReliable2,
        SoeMessageType.cUdpPacketReliable3,
        SoeMessageType.cUdpPacketReliable4,
        SoeMessageType.cUdpPacketFragment1,
        SoeMessageType.cUdpPacketFragment2,
        SoeMessageType.cUdpPacketFragment3,
        SoeMessageType.cUdpPacketFragment4})
public class ReliableMessageController extends BaseSoeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReliableMessageController.class);

    @Override
    public void handleIncoming(byte zeroByte, SoeMessageType type, SoeUdpConnection connection, ByteBuffer buffer) {

        short sequenceNum = buffer.getShort();
        LOGGER.trace("{} Receiving Reliable Message Sequence {} {}", connection.getId(), sequenceNum, buffer.order());
        connection.sendAck(sequenceNum);

        if(type == SoeMessageType.cUdpPacketFragment1 ||
                type == SoeMessageType.cUdpPacketFragment2 ||
                type == SoeMessageType.cUdpPacketFragment3 ||
                type == SoeMessageType.cUdpPacketFragment4) {

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
