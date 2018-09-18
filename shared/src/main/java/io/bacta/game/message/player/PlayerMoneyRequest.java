package io.bacta.game.message.player;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Requests the player's money be updated by the server.
 */
@Getter
@Priority(0x1)
public final class PlayerMoneyRequest extends GameNetworkMessage {
    public PlayerMoneyRequest(final ByteBuffer buffer) {
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
    }
}
