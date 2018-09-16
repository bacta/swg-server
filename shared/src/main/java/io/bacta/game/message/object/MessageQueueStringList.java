package io.bacta.game.message.object;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.engine.buffer.ByteBufferWritable;
import io.bacta.game.GameControllerMessage;
import io.bacta.game.GameControllerMessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by crush on 5/30/2016.
 */
@Getter
@AllArgsConstructor
@GameControllerMessage(GameControllerMessageType.NPC_CONVERSATION_RESPONSES)
public final class MessageQueueStringList implements ByteBufferWritable {
    private final List<String> strings; //vector of unicode strings.

    public MessageQueueStringList(final ByteBuffer buffer) {
        //This struct has some custom logic for packing a string where the size is only a byte instead of an int.
        final byte size = buffer.get();
        this.strings = new ArrayList<>(size);

        for (int i = 0; i < size; ++i)
            this.strings.add(BufferUtil.getUnicode(buffer));
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        final byte size = (byte) strings.size();
        BufferUtil.put(buffer, size);

        for (int i = 0; i < size; ++i)
            BufferUtil.putUnicode(buffer, strings.get(i));
    }
}
