package com.ocdsoft.bacta.engine.network;

import com.ocdsoft.bacta.engine.buffer.ByteBufferWritable;
import lombok.Getter;

import java.nio.ByteBuffer;

public enum ServerStatus implements ByteBufferWritable {

	DOWN (0),
    LOADING (1),
    UP (2),
    LOCKED(3),
    RESTRICTED(4),
    FULL(5);

    @Getter
    private int value;

    ServerStatus(int value) {
        this.value = value;
    }

    @Override
    public void writeToBuffer(ByteBuffer buffer) {
        buffer.putInt(value);
    }
}
