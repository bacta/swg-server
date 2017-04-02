package com.ocdsoft.bacta.swg.protocol.controller;

import com.ocdsoft.bacta.swg.protocol.connection.SoeUdpConnection;
import com.ocdsoft.bacta.swg.protocol.message.GameNetworkMessage;

public interface GameNetworkMessageController<Data extends GameNetworkMessage>  {
    void handleIncoming(SoeUdpConnection connection, Data message) throws Exception;
}
