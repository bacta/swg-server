package io.bacta.login.message;

import io.bacta.shared.GameNetworkMessage;
import io.bacta.shared.crypto.KeyShare;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

/**
 * This message comes from the login server, and adds a new encryption key to the galaxy server.
 */
@Data
@RequiredArgsConstructor
public final class GalaxyEncryptionKey extends GameNetworkMessage {
    private final KeyShare.Key key;

    public GalaxyEncryptionKey(ByteBuffer buffer) {
        this.key = new KeyShare.Key(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        key.writeToBuffer(buffer);
    }
}