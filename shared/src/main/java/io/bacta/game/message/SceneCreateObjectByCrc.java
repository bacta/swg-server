package io.bacta.game.message;


import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.swg.math.Transform;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

@RequiredArgsConstructor
@Priority(0x5)
public final class SceneCreateObjectByCrc extends GameNetworkMessage {
    private final long networkId;
    private final Transform transform;
    private final int crc;
    private final boolean hyperspace;

    public SceneCreateObjectByCrc(final ByteBuffer buffer) {
        networkId = buffer.getLong();
        transform = new Transform(buffer);
        crc = buffer.getInt();
        hyperspace = BufferUtil.getBoolean(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        buffer.putLong(networkId);  // NetworkID
        transform.writeToBuffer(buffer);
        buffer.putInt(crc); // Client ObjectCRC
        BufferUtil.put(buffer, hyperspace);
    }
}
