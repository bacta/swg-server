package io.bacta.game.message.object;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.GameControllerMessage;
import io.bacta.game.GameControllerMessageType;
import io.bacta.game.MessageQueueData;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 5/29/2016.
 */
@Getter
@AllArgsConstructor
@GameControllerMessage(GameControllerMessageType.SET_POSTURE)
public final class MessageQueuePosture implements MessageQueueData {
    private final byte posture;
    private final boolean isClientImmediate;

    public MessageQueuePosture(final ByteBuffer buffer) {
        posture = buffer.get();
        isClientImmediate = BufferUtil.getBoolean(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, posture);
        BufferUtil.put(buffer, isClientImmediate);
    }
}
