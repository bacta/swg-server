package io.bacta.game.message.player;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Requests the player's guild info be sent from the server.
 */
@Getter
@Priority(0x2)
public final class GuildRequestMessage extends GameNetworkMessage {
    public GuildRequestMessage(final ByteBuffer buffer) {
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
    }
}
