package io.bacta.game.message.planet;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.engine.buffer.ByteBufferWritable;
import io.bacta.swg.math.Vector2d;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 5/28/2016.
 */
@Getter
@AllArgsConstructor
public final class MapLocation implements ByteBufferWritable {
    public static final int INACTIVE = 0x0001;
    public static final int ACTIVE = 0x0002;

    private final long locationId;
    private final String locationName; //unicode
    private final Vector2d location;
    private final byte category;
    private final byte subCategory;
    private final byte flags;
    private final float size; //Size is not part of the buffer struct.

    public MapLocation(final ByteBuffer buffer) {
        locationId = buffer.getLong();
        locationName = BufferUtil.getAscii(buffer);
        location = new Vector2d(buffer);
        category = buffer.get();
        subCategory = buffer.get();
        flags = buffer.get();
        size = 0.f;
    }

    public boolean isInactive() {
        return (flags & INACTIVE) != 0;
    }

    public boolean isActive() {
        return (flags & ACTIVE) != 0;
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        BufferUtil.put(buffer, locationId);
        BufferUtil.putUnicode(buffer, locationName);
        BufferUtil.put(buffer, location);
        BufferUtil.put(buffer, category);
        BufferUtil.put(buffer, subCategory);
        BufferUtil.put(buffer, flags);
    }
}
