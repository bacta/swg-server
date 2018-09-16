package io.bacta.game.message;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

@Getter
@AllArgsConstructor
@Priority(0x3)
public final class SceneDestroyObject extends GameNetworkMessage {
    private final long objectId;
    private final boolean hyperspace;

    public SceneDestroyObject(final ByteBuffer buffer) {
        this.objectId = buffer.getLong();
        this.hyperspace = BufferUtil.getBoolean(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        buffer.putLong(objectId);  // ObjectID
        BufferUtil.put(buffer, hyperspace);
    }
}
