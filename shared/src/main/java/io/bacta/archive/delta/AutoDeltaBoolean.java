package io.bacta.archive.delta;

import io.bacta.engine.buffer.BufferUtil;

import java.nio.ByteBuffer;

public class AutoDeltaBoolean extends AutoDeltaVariableBase {
    private boolean currentValue;
    private transient boolean lastValue;

    public AutoDeltaBoolean() {
    }

    public AutoDeltaBoolean(boolean value) {
        super();

        this.currentValue = value;
        this.lastValue = value;
    }

    public boolean get() {
        return this.currentValue;
    }

    public void set(boolean value) {
        if (this.currentValue != value) {
            this.currentValue = value;
            touch();
        }
    }

    public void clearDelta() {
        this.lastValue = this.currentValue;
    }

    public boolean isDirty() {
        return this.currentValue != this.lastValue;
    }

    @Override
    public void packDelta(ByteBuffer buffer) {
        pack(buffer);
        clearDelta();
    }

    @Override
    public void unpackDelta(ByteBuffer buffer) {
        this.currentValue = BufferUtil.getBoolean(buffer);
        touch();
    }

    @Override
    public void pack(ByteBuffer buffer) {
        BufferUtil.put(buffer, this.currentValue);
    }

    @Override
    public void unpack(ByteBuffer buffer) {
        this.currentValue = BufferUtil.getBoolean(buffer);
        clearDelta();
    }
}
