package com.ocdsoft.bacta.soe.protocol.controller;

import com.ocdsoft.bacta.soe.protocol.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.protocol.message.GameNetworkMessage;

public interface GameNetworkMessageController<Data extends GameNetworkMessage>  {
    void handleIncoming(SoeUdpConnection connection, Data message) throws Exception;
}
