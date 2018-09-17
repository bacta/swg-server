package io.bacta.game.message.object;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.GameControllerMessage;
import io.bacta.game.GameControllerMessageType;
import io.bacta.game.MessageQueueData;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 5/30/2016.
 * <p>
 * A spatial chat message is issued on an server object's MessageQueue if the object
 * observes 'source' speaking to 'target'.
 * <p>
 * A spatial chat message is issued on an client object's MessageQueue if it
 * wants to speak.
 */
@Getter
@AllArgsConstructor
@GameControllerMessage({
        GameControllerMessageType.SPATIAL_CHAT_SEND,
        GameControllerMessageType.SPATIAL_CHAT_RECEIVE
})
public final class MessageQueueSpatialChat implements MessageQueueData {
    private final long sourceId;
    private final long targetId;
    private final String text; //unicode
    private final short volume;
    private final short chatType;
    private final short moodType;
    private final int flags;
    private final byte language;
    private final String outOfBand; //unicode
    private final String sourceName; //unicode

    public MessageQueueSpatialChat(final ByteBuffer buffer) {
        sourceId = buffer.getLong();
        targetId = buffer.getLong();
        text = BufferUtil.getUnicode(buffer);
        flags = buffer.getInt();
        volume = buffer.getShort();
        chatType = buffer.getShort();
        moodType = buffer.getShort();
        language = buffer.get();
        outOfBand = BufferUtil.getUnicode(buffer);
        sourceName = BufferUtil.getUnicode(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, sourceId);
        BufferUtil.put(buffer, targetId);
        BufferUtil.putUnicode(buffer, text);
        BufferUtil.put(buffer, flags);
        BufferUtil.put(buffer, volume);
        BufferUtil.put(buffer, chatType);
        BufferUtil.put(buffer, moodType);
        BufferUtil.put(buffer, language);
        BufferUtil.putUnicode(buffer, outOfBand);
        BufferUtil.putUnicode(buffer, sourceName);
    }

    public static final class Flags {
        public static final int PRIVATE = 0x0001; //Message text is intended for target only.
        public static final int SKIP_TARGET = 0x0002;
        public static final int SKIP_SOURCE = 0x0004;
        public static final int TARGET_ONLY = 0x0008;
        public static final int TARGET_GROUP_ONLY = 0x0010;
        public static final int SHIP_PILOT = 0x0020;
        public static final int SHIP_OPERATIONS = 0x0040;
        public static final int SHIP_GUNNER = 0x0080;
        public static final int SHIP = SHIP_PILOT | SHIP_OPERATIONS | SHIP_GUNNER;
        public static final int TARGET_AND_SOURCE_GROUP = 0x01000;
    }
}
