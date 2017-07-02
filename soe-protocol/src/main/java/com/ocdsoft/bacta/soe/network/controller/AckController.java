package com.ocdsoft.bacta.soe.network.controller;

import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.network.message.SoeMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

@SoeController(handles = {SoeMessageType.cUdpPacketAck1, SoeMessageType.cUdpPacketAck2, SoeMessageType.cUdpPacketAck3, SoeMessageType.cUdpPacketAck4})
public class AckController extends BaseSoeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AckController.class);

    @Override
    public void handleIncoming(byte zeroByte, SoeMessageType type, SoeUdpConnection connection, ByteBuffer buffer) throws Exception {
        short sequenceNum = buffer.getShort();
        connection.sendAck(sequenceNum);
        LOGGER.trace("{} Client Ack for Sequence {} {}", connection.getId(), sequenceNum, buffer.order());
    }
}
