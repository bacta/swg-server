package bacta.io.soe.network.controller;

import bacta.io.network.controller.MessageController;
import bacta.io.soe.network.connection.SoeUdpConnection;
import bacta.io.soe.network.dispatch.GameNetworkMessageDispatcher;
import bacta.io.soe.network.dispatch.SoeMessageDispatcher;
import bacta.io.soe.network.message.SoeMessageType;

import java.nio.ByteBuffer;

public interface SoeMessageController extends MessageController {
    void handleIncoming(byte zeroByte, SoeMessageType type, SoeUdpConnection connection, ByteBuffer buffer) throws Exception;
    void setSoeMessageDispatcher(SoeMessageDispatcher soeMessageDispatcher);
    void setGameNetworkMessageDispatcher(GameNetworkMessageDispatcher gameNetworkMessageDispatcher);
}
