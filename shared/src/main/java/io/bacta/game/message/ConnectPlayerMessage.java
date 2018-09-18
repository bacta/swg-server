package io.bacta.game.message;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

/**
 * This message is apparently meant to connect the player to the customer service server.
 * 00 00 00 00
 * <p>
 * SOECRC32.hashCode(ConnectPlayerMessage.class.getSimpleName()); // 0x2e365218
 */
@Getter
@Priority(0x2)
@RequiredArgsConstructor
public final class ConnectPlayerMessage extends GameNetworkMessage {
    private final int accountId;

    public ConnectPlayerMessage(final ByteBuffer buffer) {
        accountId = buffer.getInt();
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.put(buffer, accountId);
    }
}
