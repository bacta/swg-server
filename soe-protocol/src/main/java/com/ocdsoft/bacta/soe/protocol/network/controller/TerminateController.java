package com.ocdsoft.bacta.soe.protocol.network.controller;

import com.ocdsoft.bacta.engine.buffer.UnsignedUtil;
import com.ocdsoft.bacta.soe.protocol.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.protocol.network.message.TerminateReason;
import com.ocdsoft.bacta.soe.protocol.network.message.UdpPacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

@SoeController(handles = {UdpPacketType.cUdpPacketTerminate})
public class TerminateController extends BaseSoeController {

    private static final Logger logger = LoggerFactory.getLogger(TerminateController.class);

    @Override
    public void handleIncoming(byte zeroByte, UdpPacketType type, SoeUdpConnection connection, ByteBuffer buffer) {

        long connectionID = UnsignedUtil.getUnsignedInt(buffer);
        TerminateReason terminateReason = TerminateReason.values()[buffer.getShort()];

        if(connectionID == connection.getId()) {
            connection.terminate(terminateReason, true);
        }
    }
}