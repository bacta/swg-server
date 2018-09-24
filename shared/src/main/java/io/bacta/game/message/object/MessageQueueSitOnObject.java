package io.bacta.game.message.object;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.GameControllerMessage;
import io.bacta.game.GameControllerMessageType;
import io.bacta.game.MessageQueueData;
import io.bacta.swg.math.Vector;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 5/30/2016.
 * <p>
 * A message to instruct a CreatureObject to sit on another Object.
 * <p>
 * This function is intended for use with the Postures::Sitting posture
 * (e.g. sitting on a chair or couch).  It is not meant to handle the action
 * of mounting an Object.
 */
@Getter
@AllArgsConstructor
@GameControllerMessage(GameControllerMessageType.SIT_ON_OBJECT)
public final class MessageQueueSitOnObject implements MessageQueueData {
    public static final float MAXIMUM_CHAIR_RANGE = 2.0f;
    public static final float MAXIMUM_CHAIR_RANGE_SQUARED = MAXIMUM_CHAIR_RANGE * MAXIMUM_CHAIR_RANGE;

    private final long chairCellId;
    private final Vector chairPositionInParent;

    public MessageQueueSitOnObject(final ByteBuffer buffer) {
        chairCellId = buffer.getLong();
        chairPositionInParent = new Vector(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, chairCellId);
        BufferUtil.put(buffer, chairPositionInParent);
    }
}
