package com.ocdsoft.bacta.engine.buffer;

import java.io.Serializable;
import java.nio.ByteBuffer;

/**
 * The ByteBufferWritable interface represents an object that can be serialized into a {@link ByteBuffer}.
 */
public interface ByteBufferWritable extends Serializable {

    /**
     * Writes the object to a {@link ByteBuffer}'s byte buffer.
     *
     * @param buffer The message to which this object will be written.
     */
    void writeToBuffer(final ByteBuffer buffer);
}
