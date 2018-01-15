package io.bacta.login.message;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.shared.GameNetworkMessage;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

/**
 * This message comes from the login server, and adds a new encryption key to the galaxy server.
 */
@Data
@RequiredArgsConstructor
public final class GalaxyEncryptionKey extends GameNetworkMessage {
    private final String key;

    public GalaxyEncryptionKey(ByteBuffer buffer) {
        this.key = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.putAscii(buffer, key);
    }
}