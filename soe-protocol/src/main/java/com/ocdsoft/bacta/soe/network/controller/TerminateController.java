package com.ocdsoft.bacta.soe.network.controller;

import com.ocdsoft.bacta.engine.buffer.UnsignedUtil;
import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.network.message.TerminateReason;
import com.ocdsoft.bacta.soe.network.message.SoeMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

@Component
@SoeController(handles = {SoeMessageType.cUdpPacketTerminate})
public class TerminateController extends BaseSoeController {

    private static final Logger logger = LoggerFactory.getLogger(TerminateController.class);

    @Override
    public void handleIncoming(byte zeroByte, SoeMessageType type, SoeUdpConnection connection, ByteBuffer buffer) {

        long connectionID = UnsignedUtil.getUnsignedInt(buffer);
        TerminateReason terminateReason = TerminateReason.values()[buffer.getShort()];

        if(connectionID == connection.getId()) {
            connection.terminate(terminateReason, true);
        }
    }
}