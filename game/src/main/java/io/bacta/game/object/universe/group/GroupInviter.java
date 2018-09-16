package io.bacta.game.object.universe.group;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.engine.buffer.ByteBufferWritable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 8/12/2014.
 */
@Getter
@AllArgsConstructor
public final class GroupInviter implements ByteBufferWritable {
    private final long inviterId;
    private final String name;
    private final long inviterShipId;

    public GroupInviter() {
        inviterId = 0;
        name = "";
        inviterShipId = 0;
    }

    public GroupInviter(final ByteBuffer buffer) {
        this.inviterId = buffer.getLong();
        this.name = BufferUtil.getAscii(buffer);
        this.inviterShipId = buffer.getLong();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.put(buffer, inviterId);
        BufferUtil.putAscii(buffer, name);
        BufferUtil.put(buffer, inviterShipId);
    }
}
