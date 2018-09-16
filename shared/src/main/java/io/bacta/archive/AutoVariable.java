package io.bacta.archive;

import io.bacta.engine.buffer.ByteBufferWritable;

import java.nio.ByteBuffer;

/**
 * Created by crush on 8/14/2014.
 */
public class AutoVariable<T extends ByteBufferWritable> implements AutoVariableBase {
    private T value;

    public AutoVariable(T value) {
        this.value = value;
    }

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

    @Override
    public void pack(ByteBuffer buffer) {
        value.writeToBuffer(buffer);
    }

    @Override
    public void unpack(ByteBuffer buffer) {

    }
}
