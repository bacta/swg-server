package com.ocdsoft.bacta.soe.protocol.network.message;

import java.nio.ByteBuffer;

public final class MultiMessage extends SoeMessage {

    public MultiMessage(ByteBuffer inbuffer1, ByteBuffer inbuffer2) {
        super(UdpPacketType.cUdpPacketMulti);

        add(inbuffer1);
        add(inbuffer2);
    }

    public void add(ByteBuffer inbuffer) {

        assert inbuffer.remaining() <= 0xFF : "Buffer is too large ( > 0xFF ) and should never have reached this";

        int byteCount = inbuffer.remaining();
        if(byteCount > 0xFF) {
            byte sizeCount = (byte)((byteCount / 0xFF) - (byteCount % 0xFF == 0 ? 1 : 0));

            buffer.put((byte) 0xFF);
            buffer.put(sizeCount);
            byteCount -= 0xFF;

            for (int i = 0; i < sizeCount; ++i) {
                buffer.put(byteCount > 0xFF ? (byte) 0xFF : (byte) byteCount);
                byteCount -= 0xFF;
            }

        } else {
            buffer.put((byte) byteCount);
        }

        buffer.put(inbuffer);
    }
}
