package com.ocdsoft.bacta.soe.protocol.network.controller;

import com.ocdsoft.bacta.soe.protocol.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.protocol.network.message.GameNetworkMessage;

/**
 * Created by crush on 5/27/2016.
 */
public interface GameClientMessageController<T extends GameNetworkMessage> {
    void handleIncoming(long[] distributionList, boolean reliable, T message, SoeUdpConnection connection) throws Exception;
}
