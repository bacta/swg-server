package io.bacta.game.message.object;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.GameControllerMessage;
import io.bacta.game.GameControllerMessageType;
import io.bacta.game.MessageQueueData;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.nio.ByteBuffer;

@Getter
@RequiredArgsConstructor
@GameControllerMessage(GameControllerMessageType.SET_GROUP_INVITER)
public final class MessageQueueSetGroupInviter implements MessageQueueData {
    private final String inviterName; //ascii
    private final long inviterNetworkId;
    private final long inviterShipNetworkId;

    public MessageQueueSetGroupInviter(ByteBuffer buffer) {
        inviterName = BufferUtil.getAscii(buffer);
        inviterNetworkId = buffer.getLong();
        inviterShipNetworkId = buffer.getLong();
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.putAscii(buffer, inviterName);
        BufferUtil.put(buffer, inviterNetworkId);
        BufferUtil.put(buffer, inviterShipNetworkId);
    }
}
