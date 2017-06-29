package com.ocdsoft.bacta.chat.message;

import com.ocdsoft.bacta.soe.protocol.network.message.GameNetworkMessage;
import lombok.AllArgsConstructor;

import java.nio.ByteBuffer;

/**
 * Created by crush on 5/20/2016.
 * <p>
 * This class has no members. It is just a message that tells the client that the connection has been established
 * between the ChatServer and GameServer.
 */
//@Priority(0x05)
@AllArgsConstructor
public final class ChatOnConnectAvatar extends GameNetworkMessage {
    public ChatOnConnectAvatar(final ByteBuffer buffer) {
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
    }
}