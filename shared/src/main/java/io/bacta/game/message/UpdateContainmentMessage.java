package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.Getter;

import java.nio.ByteBuffer;

@Getter
@Priority(0x4)
public final class UpdateContainmentMessage extends GameNetworkMessage {

    private final long objectId;
    private final long containerId;
    private final int slotArrangement;

    public UpdateContainmentMessage(long objectId) {
        this.objectId = objectId;//object.getNetworkId();
        this.containerId = 0;//object.getContainedBy();
        this.slotArrangement = 4;//object.getCurrentArrangement();
    }

    public UpdateContainmentMessage(final ByteBuffer buffer) {
        this.objectId = buffer.getLong();
        this.containerId = buffer.getLong();
        this.slotArrangement = buffer.getInt();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        buffer.putLong(objectId);
        buffer.putLong(containerId);
        buffer.putInt(slotArrangement);
    }
}
