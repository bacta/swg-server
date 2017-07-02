package com.ocdsoft.bacta.soe.network.message;


import com.ocdsoft.bacta.engine.buffer.BufferUtil;
import lombok.AllArgsConstructor;

import java.nio.ByteBuffer;

@AllArgsConstructor
@Priority(0x3)
public final class ErrorMessage extends GameNetworkMessage {

    private final String errorName;
    private final String description;
    private final boolean fatal;

    public ErrorMessage(final ByteBuffer buffer) {
        this.errorName = BufferUtil.getAscii(buffer);
        this.description = BufferUtil.getAscii(buffer);
        this.fatal = BufferUtil.getBoolean(buffer);
    }

    @Override
    public void writeToBuffer(final ByteBuffer buffer) {
        BufferUtil.putAscii(buffer, errorName);
        BufferUtil.putAscii(buffer, description);
        BufferUtil.put(buffer, fatal);
    }
}
