package io.bacta.game.waypoint;

import io.bacta.engine.buffer.BufferUtil;
import io.bacta.engine.buffer.ByteBufferWritable;
import io.bacta.swg.util.NetworkId;
import io.bacta.swg.utility.Location;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.nio.ByteBuffer;

@Getter
@AllArgsConstructor
public class WaypointDataBase implements ByteBufferWritable {
    int appearanceNameCrc;
    Location location;
    String name;
    byte color;
    boolean active;

    public WaypointDataBase() {
        color = WaypointType.BLUE;
        location = new Location();
        name = "";
    }

    public WaypointDataBase(final ByteBuffer buffer) {
        appearanceNameCrc = buffer.getInt();
        location = new Location(buffer);
        name = BufferUtil.getUnicode(buffer);
        final long networkId = buffer.getLong(); //preserve format of old persisted byetstreams
        color = buffer.get();
        active = BufferUtil.getBoolean(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        buffer.putInt(appearanceNameCrc);
        location.writeToBuffer(buffer);
        BufferUtil.putUnicode(buffer, name);
        buffer.putLong(NetworkId.INVALID); //preserve format of old persisted bytestreams
        buffer.put(color);
        BufferUtil.put(buffer, active);
    }

    public void setName(final String name) {
        //SOE had 250 character limit on waypoint names. seems reasonable.
        if (name.length() > 250) {
            this.name = name.substring(0, 250);
        } else {
            this.name = name;
        }
    }
}
