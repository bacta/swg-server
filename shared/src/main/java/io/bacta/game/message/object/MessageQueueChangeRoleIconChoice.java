package io.bacta.game.message.object;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.engine.buffer.ByteBufferWritable;
import io.bacta.game.GameControllerMessage;
import io.bacta.game.GameControllerMessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 6/4/2016.
 */
@Getter
@AllArgsConstructor
@GameControllerMessage(GameControllerMessageType.CHANGE_ROLE_ICON_CHOICE)
public final class MessageQueueChangeRoleIconChoice implements ByteBufferWritable {
    private final int roleIconChoice;
    private final byte sequenceId;

    public MessageQueueChangeRoleIconChoice(final ByteBuffer buffer) {
        roleIconChoice = buffer.getInt();
        sequenceId = buffer.get();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, roleIconChoice);
        BufferUtil.put(buffer, sequenceId);
    }
}
