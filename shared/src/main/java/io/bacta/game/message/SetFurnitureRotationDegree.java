package io.bacta.game.message;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

/**
 * 5A 00 00 00
 * <p>
 * SOECRC32.hashCode(SetFurnitureRotationDegree.class.getSimpleName()); // 0x91419229
 */
@Getter
@Priority(0x2)
@RequiredArgsConstructor
public final class SetFurnitureRotationDegree extends GameNetworkMessage {
    private final int value;

    public SetFurnitureRotationDegree(final ByteBuffer buffer) {
        value = buffer.getInt();
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.put(buffer, value);
    }
}
