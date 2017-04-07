package com.ocdsoft.bacta.soe.protocol.serialize;

import com.ocdsoft.bacta.soe.protocol.network.message.GameNetworkMessage;

import java.nio.ByteBuffer;

/**
 * Created by kyle on 5/1/2016.
 */
public interface GameNetworkMessageSerializer {
    <T extends GameNetworkMessage> ByteBuffer writeToBuffer(T object);
    <T extends GameNetworkMessage> T readFromBuffer(final int messageType, final ByteBuffer buffer);
}
