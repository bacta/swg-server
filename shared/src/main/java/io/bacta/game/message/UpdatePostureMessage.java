package io.bacta.game.message;

import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 5/18/2016.
 */
@Getter
@AllArgsConstructor
@Priority(0x03)
public final class UpdatePostureMessage extends GameNetworkMessage {
    private final long target;
    private final byte posture;

    public UpdatePostureMessage(final ByteBuffer buffer) {
        this.posture = buffer.get();
        this.target = buffer.getLong();
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        buffer.put(posture);
        buffer.putLong(target);
    }
}
