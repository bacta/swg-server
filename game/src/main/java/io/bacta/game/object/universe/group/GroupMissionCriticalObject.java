package io.bacta.game.object.universe.group;

import io.bacta.engine.buffer.ByteBufferWritable;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

@Getter
@AllArgsConstructor
public class GroupMissionCriticalObject implements ByteBufferWritable {

    private long unknown1;
    private long unknown2;

    public GroupMissionCriticalObject(final ByteBuffer buffer) {
        unknown1 = buffer.getLong();
        unknown2 = buffer.getLong();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        buffer.putLong(unknown1);
        buffer.putLong(unknown2);
    }
}
