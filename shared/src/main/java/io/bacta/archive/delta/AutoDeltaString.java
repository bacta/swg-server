package io.bacta.archive.delta;

import io.bacta.engine.buffer.BufferUtil;

import java.nio.ByteBuffer;

public class AutoDeltaString extends AutoDeltaVariableBase {
    private String currentValue;
    private transient String lastValue;

    public AutoDeltaString() {
        currentValue = "";
        lastValue = "";
    }

    public AutoDeltaString(String value) {
        super();

        this.currentValue = value;
        this.lastValue = value;
    }

    public String get() {
        return this.currentValue;
    }

    public void set(String value) {
        if (!this.currentValue.equals(value)) {
            this.currentValue = value;
            touch();
        }
    }

    public void clearDelta() {
        this.lastValue = this.currentValue;
    }

    public boolean isDirty() {
        return !this.currentValue.equals(this.lastValue);
    }

    @Override
    public void packDelta(ByteBuffer buffer) {
        pack(buffer);
        clearDelta();
    }

    @Override
    public void unpackDelta(ByteBuffer buffer) {
        this.currentValue = BufferUtil.getAscii(buffer);
        touch();
    }

    @Override
    public void pack(ByteBuffer buffer) {
        BufferUtil.putAscii(buffer, this.currentValue);
    }

    @Override
    public void unpack(ByteBuffer buffer) {
        this.currentValue = BufferUtil.getAscii(buffer);
        clearDelta();
    }
}
