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
 * A teleport ack message is sent from a client to a game server to
 * acknowledge receipt of a teleport message.
 */
@Getter
@AllArgsConstructor
@GameControllerMessage(GameControllerMessageType.TELEPORT_ACK)
public final class MessageQueueTeleportAck implements MessageQueueData {
    private final int sequenceId;

    public MessageQueueTeleportAck(final ByteBuffer buffer) {
        sequenceId = buffer.getInt();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, sequenceId);
    }
}
