package com.ocdsoft.bacta.soe.protocol.network.controller;

import com.ocdsoft.bacta.engine.io.network.controller.MessageController;
import com.ocdsoft.bacta.soe.protocol.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.protocol.network.message.GameNetworkMessage;

public interface GameNetworkMessageController<Data extends GameNetworkMessage> extends MessageController {
    void handleIncoming(SoeUdpConnection connection, Data message) throws Exception;
}
