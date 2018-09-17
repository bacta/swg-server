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
 */
@Getter
@AllArgsConstructor
@GameControllerMessage(GameControllerMessageType.SECURE_TRADE)
public final class MessageQueueSecureTrade implements MessageQueueData {
    private final TradeMessageId id;
    private final long initiator;
    private final long recipient;

    public MessageQueueSecureTrade(final ByteBuffer buffer) {
        id = TradeMessageId.from(buffer.get());
        initiator = buffer.getLong();
        recipient = buffer.getLong();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, id.value);
        BufferUtil.put(buffer, initiator);
        BufferUtil.put(buffer, recipient);
    }

    public enum TradeMessageId {
        REQUEST_TRADE(0),
        TRADE_REQUESTED(1),
        ACCEPT_TRADE(2),
        DENIED_TRADE(3),
        DENIED_PLAYER_BUSY(4),
        DENIED_PLAYER_UNREACHABLE(5),
        REQUEST_TRADE_REVERSED(6);

        private static final TradeMessageId[] values = values();
        public final int value;

        TradeMessageId(final int value) {
            this.value = value;
        }

        public static TradeMessageId from(final int value) {
            if (value < 0 || value >= values.length)
                return values[0];

            return values[value];
        }
    }
}
