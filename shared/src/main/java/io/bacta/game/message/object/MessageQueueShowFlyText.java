package io.bacta.game.message.object;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.GameControllerMessage;
import io.bacta.game.GameControllerMessageType;
import io.bacta.game.MessageQueueData;
import io.bacta.swg.localization.StringId;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 5/30/2016.
 * <p>
 * A message to instruct a client object to place fly text over it.
 * <p>
 * Fly text is the text that shows up over an object, like the damage
 * numbers over combatants, that moves upward and fades out over time.
 */
@Getter
@AllArgsConstructor
@GameControllerMessage(GameControllerMessageType.SHOW_FLY_TEXT)
public final class MessageQueueShowFlyText implements MessageQueueData {
    private final long emitterId;
    private final StringId outputTextId;
    private final String outputTextOutOfBand; //unicode
    private final float scale;
    private final int red;
    private final int green;
    private final int blue;
    private final int flags;


    public MessageQueueShowFlyText(final ByteBuffer buffer) {
        emitterId = buffer.getLong();
        outputTextId = new StringId(buffer);
        outputTextOutOfBand = BufferUtil.getUnicode(buffer);
        scale = buffer.getFloat();
        red = buffer.getInt();
        green = buffer.getInt();
        blue = buffer.getInt();
        flags = buffer.getInt();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, emitterId);
        BufferUtil.put(buffer, outputTextId);
        BufferUtil.putUnicode(buffer, outputTextOutOfBand);
        BufferUtil.put(buffer, scale);
        BufferUtil.put(buffer, red);
        BufferUtil.put(buffer, green);
        BufferUtil.put(buffer, blue);
        BufferUtil.put(buffer, flags);
    }

    public static final class Flags {
        public static final int PRIVATE = 0x0001;
        public static final int SHOW_IN_CHAT_BOX = 0x0002;
        public static final int DAMAGE_FROM_PLAYER = 0x0004;
        public static final int SNARE = 0x0008;
        public static final int GLANCING_BLOW = 0x0010;
        public static final int CRITICAL_HIT = 0x0020;
        public static final int LUCKY = 0x0040;
        public static final int DOT = 0x0080;
        public static final int BLEED = 0x0100;
        public static final int HEAL = 0x0200;
        public static final int FREESHOT = 0x0400;
    }
}
