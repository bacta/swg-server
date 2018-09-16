package io.bacta.archive.delta;

import java.nio.ByteBuffer;

public class AutoDeltaShort extends AutoDeltaVariableBase {
    private short currentValue;
    private transient short lastValue;

    public AutoDeltaShort() {
    }

    public AutoDeltaShort(short value) {
        super();

        this.currentValue = value;
        this.lastValue = value;
    }

    public short get() {
        return this.currentValue;
    }

    public void set(short value) {
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
        this.currentValue = buffer.getShort();
        touch();
    }

    @Override
    public void pack(ByteBuffer buffer) {
        buffer.putShort(this.currentValue);
    }

    @Override
    public void unpack(ByteBuffer buffer) {
        this.currentValue = buffer.getShort();
        clearDelta();
    }
}
