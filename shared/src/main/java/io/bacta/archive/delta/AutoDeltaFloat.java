package io.bacta.archive.delta;

import java.nio.ByteBuffer;

public class AutoDeltaFloat extends AutoDeltaVariableBase {
    private float currentValue;
    private transient float lastValue;

    public AutoDeltaFloat() {
    }

    public AutoDeltaFloat(float value) {
        super();

        this.currentValue = value;
        this.lastValue = value;
    }

    public float get() {
        return this.currentValue;
    }

    public void set(float value) {
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
        this.currentValue = buffer.getFloat();
        touch();
    }

    @Override
    public void pack(ByteBuffer buffer) {
        buffer.putFloat(this.currentValue);
    }

    @Override
    public void unpack(ByteBuffer buffer) {
        this.currentValue = buffer.getFloat();
        clearDelta();
    }
}
