package io.bacta.game.message;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.swg.foundation.BitArray;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

@Getter
@Priority(0x05)
@AllArgsConstructor
public final class SetLfgInterests extends GameNetworkMessage {
    private final BitArray interests;

    public SetLfgInterests(final ByteBuffer buffer) {
        this.interests = new BitArray(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, interests);
    }
}