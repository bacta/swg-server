package io.bacta.archive.delta;

import java.nio.ByteBuffer;

public class AutoDeltaInt extends AutoDeltaVariableBase {
    private int currentValue;
    private transient int lastValue;

    public AutoDeltaInt() {
    }

    public AutoDeltaInt(int value) {
        super();

        this.currentValue = value;
        this.lastValue = value;
    }

    public int get() {
        return this.currentValue;
    }

    public void set(int value) {
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
        this.currentValue = buffer.getInt();
        touch();
    }

    @Override
    public void pack(ByteBuffer buffer) {
        buffer.putInt(this.currentValue);
    }

    @Override
    public void unpack(ByteBuffer buffer) {
        this.currentValue = buffer.getInt();
        clearDelta();
    }
}
