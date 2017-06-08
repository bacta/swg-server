package com.ocdsoft.bacta.soe.protocol.network.controller;

import com.ocdsoft.bacta.engine.io.network.controller.MessageController;
import com.ocdsoft.bacta.soe.protocol.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.protocol.network.dispatch.GameNetworkMessageDispatcher;
import com.ocdsoft.bacta.soe.protocol.network.dispatch.SoeMessageDispatcher;
import com.ocdsoft.bacta.soe.protocol.network.message.UdpPacketType;

import java.nio.ByteBuffer;

public interface SoeMessageController extends MessageController {
    void handleIncoming(byte zeroByte, UdpPacketType type, SoeUdpConnection connection, ByteBuffer buffer) throws Exception;
    void setSoeMessageDispatcher(SoeMessageDispatcher soeMessageDispatcher);
    void setGameNetworkMessageDispatcher(GameNetworkMessageDispatcher gameNetworkMessageDispatcher);
}
