package io.bacta.archive;

import io.bacta.engine.buffer.ByteBufferWritable;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by crush on 8/14/2014.
 */
public class AutoArray<T extends ByteBufferWritable> implements AutoVariableBase {
    private final List<T> array = new ArrayList<>();

    @Override
    public void pack(final ByteBuffer buffer) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void unpack(final ByteBuffer buffer) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
