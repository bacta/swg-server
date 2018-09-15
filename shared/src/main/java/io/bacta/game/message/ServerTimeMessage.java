package io.bacta.game.message;

import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;

import java.nio.ByteBuffer;

/**
 * Created by kyle on 5/19/2016.
 */

@AllArgsConstructor
public class ServerTimeMessage extends GameNetworkMessage {

    private final long timeSeconds;

    public ServerTimeMessage(final ByteBuffer buffer) {
        timeSeconds = buffer.getLong();
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        buffer.putLong(timeSeconds);
    }
}
