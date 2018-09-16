package io.bacta.archive.delta;

import java.nio.ByteBuffer;

public class AutoDeltaByte extends AutoDeltaVariableBase {
    private byte currentValue;
    private transient byte lastValue;

    public AutoDeltaByte() {
    }

    public AutoDeltaByte(byte value) {
        super();

        this.currentValue = value;
        this.lastValue = value;
    }

    public byte get() {
        return this.currentValue;
    }

    public void set(byte value) {
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
        this.currentValue = buffer.get();
        touch();
    }

    @Override
    public void pack(ByteBuffer buffer) {
        buffer.put(this.currentValue);
    }

    @Override
    public void unpack(ByteBuffer buffer) {
        this.currentValue = buffer.get();
        clearDelta();
    }
}
