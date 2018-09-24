package io.bacta.game.message.object;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.GameControllerMessage;
import io.bacta.game.GameControllerMessageType;
import io.bacta.game.MessageQueueData;
import io.bacta.swg.radialmenu.ObjectMenuRequestData;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.List;

@Getter
@AllArgsConstructor
@GameControllerMessage(GameControllerMessageType.OBJECT_MENU_REQUEST)
public final class MessageQueueObjectMenuRequest implements MessageQueueData {
    private final long targetId;
    private final long requestorId;
    private final List<ObjectMenuRequestData> data;
    private final byte sequenceId;

    public MessageQueueObjectMenuRequest(final ByteBuffer buffer) {
        targetId = buffer.getLong();
        requestorId = buffer.getLong();
        data = BufferUtil.getArrayList(buffer, ObjectMenuRequestData::new);
        sequenceId = buffer.get();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, targetId);
        BufferUtil.put(buffer, requestorId);
        BufferUtil.put(buffer, data);
        BufferUtil.put(buffer, sequenceId);
    }
}
