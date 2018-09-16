package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;

import java.nio.ByteBuffer;

/**
 * Created by crush on 5/18/2016.
 */
@Priority(0x4)
public final class UpdatePvpStatusMessage extends GameNetworkMessage {
    private final long target;
    private final int flags;
    private final int factionId;

    public UpdatePvpStatusMessage(final long target, final int flags, final int factionId) {
        this.target = target;
        this.flags = flags;
        this.factionId = factionId;
    }

    public UpdatePvpStatusMessage(final ByteBuffer buffer) {
        this.flags = buffer.getInt();
        this.factionId = buffer.getInt();
        this.target = buffer.getLong();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        buffer.putInt(flags);
        buffer.putInt(factionId);
        buffer.putLong(target);
    }
}
