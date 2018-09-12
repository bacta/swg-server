package io.bacta.shared.utility;

import io.bacta.engine.buffer.ByteBufferWritable;
import io.bacta.shared.tre.math.Vector;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;

/**
 * Created by crush on 5/8/2016.
 */
public class Location implements ByteBufferWritable {
    @Getter
    @Setter
    private Vector coordinates;
    @Getter
    @Setter
    private long cell;
    @Getter
    @Setter
    private int sceneIdCrc;

    public Location() {
        coordinates = new Vector();
    }

    public Location(final Vector coordinates, final long cell, final int sceneIdCrc) {
        this.coordinates = coordinates;
        this.cell = cell;
        this.sceneIdCrc = sceneIdCrc;
    }

    public Location(final ByteBuffer buffer) {
        coordinates = new Vector(buffer);
        cell = buffer.getLong();
        sceneIdCrc = buffer.getInt();
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        coordinates.writeToBuffer(buffer);
        buffer.putLong(cell);
        buffer.putInt(sceneIdCrc);
    }
}
