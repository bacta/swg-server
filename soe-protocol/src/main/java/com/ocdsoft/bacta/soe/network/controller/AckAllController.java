package com.ocdsoft.bacta.soe.network.controller;

import com.ocdsoft.bacta.soe.network.message.SoeMessageType;
import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

@SoeController(handles = {SoeMessageType.cUdpPacketAckAll1, SoeMessageType.cUdpPacketAckAll2, SoeMessageType.cUdpPacketAckAll3, SoeMessageType.cUdpPacketAckAll4})
public class AckAllController extends BaseSoeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AckAllController.class);


    @Override
    public void handleIncoming(byte zeroByte, SoeMessageType type, SoeUdpConnection connection, ByteBuffer buffer) {

        short sequenceNum = buffer.getShort();
        connection.ackAllFromClient(sequenceNum);
        LOGGER.trace("{} Client AckAll for Sequence {} {}", connection.getId(), sequenceNum, buffer.order());

    }
}
