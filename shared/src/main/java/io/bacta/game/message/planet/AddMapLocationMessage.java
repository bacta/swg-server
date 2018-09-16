package io.bacta.game.message.planet;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.game.Priority;
import io.bacta.shared.GameNetworkMessage;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 5/28/2016.
 */
@Getter
@Priority(0x05)
@AllArgsConstructor
public final class AddMapLocationMessage extends GameNetworkMessage {
    private final String planetName;
    private final long locationId;
    private final String locationName; //unicode
    private final float locationX;
    private final float locationY;
    private final byte category;
    private final byte subCategory;

    public AddMapLocationMessage(final ByteBuffer buffer) {
        this.planetName = BufferUtil.getAscii(buffer);
        this.locationId = buffer.getLong();
        this.locationName = BufferUtil.getUnicode(buffer);
        this.locationX = buffer.getFloat();
        this.locationY = buffer.getFloat();
        this.category = buffer.get();
        this.subCategory = buffer.get();
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.putAscii(buffer, planetName);
        BufferUtil.put(buffer, locationId);
        BufferUtil.putUnicode(buffer, locationName);
        BufferUtil.put(buffer, locationX);
        BufferUtil.put(buffer, locationY);
        BufferUtil.put(buffer, category);
        BufferUtil.put(buffer, subCategory);
    }
}