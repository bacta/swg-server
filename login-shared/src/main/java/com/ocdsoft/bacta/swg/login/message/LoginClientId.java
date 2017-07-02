package com.ocdsoft.bacta.swg.login.message;

import com.ocdsoft.bacta.engine.buffer.BufferUtil;
import com.ocdsoft.bacta.soe.network.message.GameNetworkMessage;
import com.ocdsoft.bacta.soe.network.message.Priority;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
    00 09 00 00 04 00 96 1F 13 41 04 00 61 73 64 66
    00 00 0E 00 32 30 30 35 30 34 30 38 2D 31 38 3A
    30 30 00 FC 79
  */
@Getter
@AllArgsConstructor
@Priority(0x4)
public final class LoginClientId extends GameNetworkMessage {
    /**
     * Serves as the id if logging in through the client directly. If the launchpad has already gained a key for
     * login, then the id can function as an integer value specifying the requested admin level of the player.
     */
    private final String id;
    /**
     * Serves as the key if logging in through the client directly. Otherwise, this should be a session key that
     * the launchpad has already gained from the login server.
     */
    private final String key;
    private final String clientVersion;

    public LoginClientId(final ByteBuffer buffer) {
        id = BufferUtil.getAscii(buffer);
        key = BufferUtil.getAscii(buffer);
        clientVersion = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.putAscii(buffer, id);
        BufferUtil.putAscii(buffer, key);
        BufferUtil.putAscii(buffer, clientVersion);
    }
}
