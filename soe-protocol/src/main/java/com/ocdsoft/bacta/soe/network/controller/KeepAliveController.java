package com.ocdsoft.bacta.soe.network.controller;

import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.network.message.SoeMessageType;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

@Component
@SoeController(handles = {SoeMessageType.cUdpPacketKeepAlive})
public class KeepAliveController extends BaseSoeController {

    @Override
    public void handleIncoming(byte zeroByte, SoeMessageType type, SoeUdpConnection connection, ByteBuffer buffer) {
        connection.updateLastActivity();
    }

}
