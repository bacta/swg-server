package com.ocdsoft.bacta.soe.network.message.login;

import com.ocdsoft.bacta.soe.network.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.network.message.game.Priority;

import java.nio.ByteBuffer;

@Priority(0x2)
public class RequestExtendedClusterInfo extends GameNetworkMessage {
    public RequestExtendedClusterInfo(final ByteBuffer buffer) {
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
    }
    /**
         02 00 05 ED 33 8E 00 00 00 00 

     */
}
