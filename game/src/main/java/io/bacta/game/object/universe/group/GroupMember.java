package io.bacta.game.object.universe.group;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.engine.buffer.ByteBufferWritable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

@Getter
@AllArgsConstructor
public final class GroupMember implements ByteBufferWritable {
    private final long memberId;
    private final String memberName;

    public GroupMember(ByteBuffer buffer) {
        memberId = buffer.getLong();
        memberName = BufferUtil.getAscii(buffer);
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        buffer.putLong(memberId);
        BufferUtil.putAscii(buffer, memberName);
    }
}
