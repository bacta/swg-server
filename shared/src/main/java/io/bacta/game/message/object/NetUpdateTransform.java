package io.bacta.game.message.object;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.GameControllerMessage;
import io.bacta.game.GameControllerMessageType;
import io.bacta.game.MessageQueueData;
import io.bacta.shared.math.Transform;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
    05 00 46 5E CE 80 21 00 00 00 71 00 00 00 01 E8
    76 48 17 00 00 00 00 00 00 00 3E 30 12 8F 01 00
    00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00
    80 3F 00 00 F0 C5 27 27 92 43 00 00 20 45 00 00
    00 00 00 00 00 00 00 

*/

@Getter
@AllArgsConstructor
@GameControllerMessage(GameControllerMessageType.NET_UPDATE_TRANSFORM)
public final class NetUpdateTransform implements MessageQueueData {

    private final int syncStamp;
    private final int sequenceNumber;
    private final Transform transform;
    private final float speed;
    private final float lookAtYaw;
    private final boolean useLookAtYaw;

    public NetUpdateTransform(final ByteBuffer buffer) {
        syncStamp = buffer.getInt();
        sequenceNumber = buffer.getInt();
        transform = new Transform(buffer);
        speed = buffer.getFloat();
        lookAtYaw = buffer.getFloat();
        useLookAtYaw = BufferUtil.getBoolean(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, syncStamp);
        BufferUtil.put(buffer, sequenceNumber);
        BufferUtil.put(buffer, transform);
        BufferUtil.put(buffer, speed);
        BufferUtil.put(buffer, lookAtYaw);
        BufferUtil.put(buffer, useLookAtYaw);
    }
}
