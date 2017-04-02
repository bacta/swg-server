package com.ocdsoft.bacta.swg.protocol.controller;

import com.ocdsoft.bacta.swg.protocol.connection.SoeUdpConnection;
import com.ocdsoft.bacta.swg.protocol.dispatch.GameNetworkMessageDispatcher;
import com.ocdsoft.bacta.swg.protocol.dispatch.SoeMessageDispatcher;
import com.ocdsoft.bacta.swg.protocol.message.UdpPacketType;

import java.nio.ByteBuffer;

public interface SoeMessageController {
    void handleIncoming(byte zeroByte, UdpPacketType type, SoeUdpConnection connection, ByteBuffer buffer) throws Exception;
    void setSoeMessageDispatcher(SoeMessageDispatcher soeMessageDispatcher);
    void setGameNetworkMessageDispatcher(GameNetworkMessageDispatcher gameNetworkMessageDispatcher);
}
