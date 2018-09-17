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
@GameControllerMessage({
        GameControllerMessageType.GET_TOKEN_FOR_OBJECT,
        GameControllerMessageType.COMBAT_AIM_TO_CLIENT,
        GameControllerMessageType.GET_WAYPOINT_FOR_OBJECT,
        GameControllerMessageType.CLIENT_RESOURCE_HARVESTER_ACTIVATE,
        GameControllerMessageType.CLIENT_RESOURCE_HARVESTER_DEACTIVATE,
        GameControllerMessageType.CLIENT_RESOURCE_HARVESTER_LISTEN,
        GameControllerMessageType.CLIENT_RESOURCE_HARVESTER_STOP_LISTENING,
        GameControllerMessageType.CLIENT_RESOURCE_HARVESTER_GET_RESOURCE_DATA,
        GameControllerMessageType.MISSION_ABORT,
        GameControllerMessageType.SET_OWNER_ID,
        GameControllerMessageType.CLIENT_LOOK_AT_TARGET,
        GameControllerMessageType.CLIENT_INTENDED_TARGET
})
public final class MessageQueueNetworkId implements MessageQueueData {
    private final long networkId;

    public MessageQueueNetworkId(final ByteBuffer buffer) {
        networkId = buffer.getLong();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, networkId);
    }
}
