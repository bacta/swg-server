package io.bacta.game.object.tangible.ship;

import io.bacta.engine.buffer.ByteBufferWritable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

@Getter
@AllArgsConstructor
public final class ShipFormationGroupMember implements ByteBufferWritable {
    private final long memberId;
    private final int memberIndex; //?

    public ShipFormationGroupMember(final ByteBuffer buffer) {
        memberId = buffer.getLong();
        memberIndex = buffer.getInt();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        buffer.putLong(memberId);
        buffer.putInt(memberIndex);
    }
}
