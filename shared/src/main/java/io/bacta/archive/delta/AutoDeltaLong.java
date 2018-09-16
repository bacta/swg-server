package io.bacta.archive.delta;

import java.nio.ByteBuffer;

public class AutoDeltaLong extends AutoDeltaVariableBase {
    private long currentValue;
    private transient long lastValue;

    public AutoDeltaLong() {
    }

    public AutoDeltaLong(long value) {
        super();

        this.currentValue = value;
        this.lastValue = value;
    }

    public long get() {
        return this.currentValue;
    }

    public void set(long value) {
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
        this.currentValue = buffer.getLong();
        touch();
    }

    @Override
    public void pack(ByteBuffer buffer) {
        buffer.putLong(this.currentValue);
    }

    @Override
    public void unpack(ByteBuffer buffer) {
        this.currentValue = buffer.getLong();
        clearDelta();
    }
}
