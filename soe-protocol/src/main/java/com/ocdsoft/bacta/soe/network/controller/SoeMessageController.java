package com.ocdsoft.bacta.soe.network.controller;

import com.ocdsoft.bacta.engine.network.controller.MessageController;
import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.network.dispatch.GameNetworkMessageDispatcher;
import com.ocdsoft.bacta.soe.network.dispatch.SoeMessageDispatcher;
import com.ocdsoft.bacta.soe.network.message.SoeMessageType;

import java.nio.ByteBuffer;

public interface SoeMessageController extends MessageController {
    void handleIncoming(byte zeroByte, SoeMessageType type, SoeUdpConnection connection, ByteBuffer buffer) throws Exception;
    void setSoeMessageDispatcher(SoeMessageDispatcher soeMessageDispatcher);
    void setGameNetworkMessageDispatcher(GameNetworkMessageDispatcher gameNetworkMessageDispatcher);
}
