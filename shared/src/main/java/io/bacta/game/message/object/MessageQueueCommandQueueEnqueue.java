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
@GameControllerMessage(GameControllerMessageType.COMMAND_QUEUE_ENQUEUE)
public class MessageQueueCommandQueueEnqueue implements MessageQueueData {
    private final int sequenceId;
    private final int commandHash;
    private final long targetId;
    private final String params; //unicode

    public MessageQueueCommandQueueEnqueue(final ByteBuffer buffer) {
        sequenceId = buffer.getInt();
        commandHash = buffer.getInt();
        targetId = buffer.getLong();
        params = BufferUtil.getUnicode(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, sequenceId);
        BufferUtil.put(buffer, commandHash);
        BufferUtil.put(buffer, targetId);
        BufferUtil.putUnicode(buffer, params);
    }
}
