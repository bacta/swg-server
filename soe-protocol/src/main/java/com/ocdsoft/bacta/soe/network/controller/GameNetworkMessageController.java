package com.ocdsoft.bacta.soe.network.controller;

import com.ocdsoft.bacta.engine.network.controller.MessageController;
import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.network.message.game.GameNetworkMessage;

public interface GameNetworkMessageController<Data extends GameNetworkMessage> extends MessageController {
    void handleIncoming(SoeUdpConnection connection, Data message) throws Exception;
}
