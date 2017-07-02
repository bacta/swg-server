package com.ocdsoft.bacta.soe.network.controller;

import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.network.message.TerminateReason;
import com.ocdsoft.bacta.soe.network.message.SoeMessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

@SoeController(handles = {SoeMessageType.cUdpPacketUnreachableConnection})
public class UnreachableController extends BaseSoeController {

    private static final Logger logger = LoggerFactory.getLogger(UnreachableController.class);

    @Override
    public void handleIncoming(byte zeroByte, SoeMessageType type, SoeUdpConnection connection, ByteBuffer buffer) {
        connection.terminate(TerminateReason.OTHERSIDETERMINATED, true);
    }
}
