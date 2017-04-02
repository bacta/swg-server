package com.ocdsoft.bacta.soe.protocol.controller;

import com.ocdsoft.bacta.soe.protocol.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.protocol.message.UdpPacketType;

import java.nio.ByteBuffer;

@SoeController(handles = {UdpPacketType.cUdpPacketKeepAlive})
public class KeepAliveController extends BaseSoeController {

    @Override
    public void handleIncoming(byte zeroByte, UdpPacketType type, SoeUdpConnection connection, ByteBuffer buffer) {
        connection.updateLastActivity();
    }

}
