package com.ocdsoft.bacta.soe.protocol.network.controller;

import com.ocdsoft.bacta.soe.protocol.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.protocol.network.message.UdpPacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

@SoeController(handles = {UdpPacketType.cUdpPacketAck1, UdpPacketType.cUdpPacketAck2, UdpPacketType.cUdpPacketAck3, UdpPacketType.cUdpPacketAck4})
public class AckController extends BaseSoeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AckController.class);

    @Override
    public void handleIncoming(byte zeroByte, UdpPacketType type, SoeUdpConnection connection, ByteBuffer buffer) throws Exception {
        short sequenceNum = buffer.getShort();
        connection.sendAck(sequenceNum);
        LOGGER.trace("{} Client Ack for Sequence {} {}", connection.getId(), sequenceNum, buffer.order());
    }
}
