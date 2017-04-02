package com.ocdsoft.bacta.swg.protocol.controller;

import com.ocdsoft.bacta.swg.protocol.connection.SoeUdpConnection;
import com.ocdsoft.bacta.swg.protocol.message.UdpPacketType;

import java.nio.ByteBuffer;

@SoeController(handles = {UdpPacketType.cUdpPacketKeepAlive})
public class KeepAliveController extends BaseSoeController {

    @Override
    public void handleIncoming(byte zeroByte, UdpPacketType type, SoeUdpConnection connection, ByteBuffer buffer) {
        connection.updateLastActivity();
    }

}
