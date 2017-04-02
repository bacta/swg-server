package com.ocdsoft.bacta.soe.protocol.controller;

import com.ocdsoft.bacta.soe.protocol.message.UdpPacketType;
import com.ocdsoft.bacta.soe.protocol.connection.SoeUdpConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

@SoeController(handles = {UdpPacketType.cUdpPacketAckAll1,UdpPacketType.cUdpPacketAckAll2, UdpPacketType.cUdpPacketAckAll3, UdpPacketType.cUdpPacketAckAll4})
public class AckAllController extends BaseSoeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AckAllController.class);


    @Override
    public void handleIncoming(byte zeroByte, UdpPacketType type, SoeUdpConnection connection, ByteBuffer buffer) {

        short sequenceNum = buffer.getShort();
        connection.ackAllFromClient(sequenceNum);
        LOGGER.trace("{} Client AckAll for Sequence {} {}", connection.getId(), sequenceNum, buffer.order());

    }
}
