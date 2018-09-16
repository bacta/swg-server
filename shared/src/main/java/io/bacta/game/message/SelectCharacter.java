package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

/**
 * Informs the game server that the client is requesting the given character id with which to play.
 */
@Getter
@Priority(0x2)
@RequiredArgsConstructor
public final class SelectCharacter extends GameNetworkMessage {
    private final long id;

    public SelectCharacter(final ByteBuffer buffer) {
        this.id = buffer.getLong();
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        buffer.putLong(this.id);
    }
}
