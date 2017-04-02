package com.ocdsoft.bacta.soe.protocol.controller;

import com.ocdsoft.bacta.soe.protocol.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.protocol.message.TerminateReason;
import com.ocdsoft.bacta.soe.protocol.message.UdpPacketType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

@SoeController(handles = {UdpPacketType.cUdpPacketUnreachableConnection})
public class UnreachableController extends BaseSoeController {

    private static final Logger logger = LoggerFactory.getLogger(UnreachableController.class);

    @Override
    public void handleIncoming(byte zeroByte, UdpPacketType type, SoeUdpConnection connection, ByteBuffer buffer) {
        connection.terminate(TerminateReason.OTHERSIDETERMINATED, true);
    }
}
