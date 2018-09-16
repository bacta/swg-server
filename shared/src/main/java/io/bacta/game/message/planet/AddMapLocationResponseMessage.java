package io.bacta.game.message.planet;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 5/28/2016.
 */
@Getter
@Priority(0x05)
@AllArgsConstructor
public final class AddMapLocationResponseMessage extends GameNetworkMessage {
    private final long locationId;

    public AddMapLocationResponseMessage(final ByteBuffer buffer) {
        locationId = buffer.getLong();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, locationId);
    }
}