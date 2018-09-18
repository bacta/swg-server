package io.bacta.game.message.player;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Requests that the players faction standings be sent to the client.
 * Expects
 */
@Getter
@Priority(0x1)
public final class FactionRequestMessage extends GameNetworkMessage {
    public FactionRequestMessage(final ByteBuffer buffer) {
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
    }
}
