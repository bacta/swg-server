package io.bacta.login.message;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.shared.GameNetworkMessage;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

/**
 * This is sent to a client connection that has just logged in a new session.
 */
@Data
@RequiredArgsConstructor
public final class SetSessionKey extends GameNetworkMessage {
    private final String sessionKey;

    public SetSessionKey(ByteBuffer buffer) {
        this.sessionKey = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.putAscii(buffer, this.sessionKey);
    }
}
