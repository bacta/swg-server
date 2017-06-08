package com.ocdsoft.bacta.soe.protocol.network.message;

import com.ocdsoft.bacta.engine.io.network.Message;
import lombok.Getter;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

@Getter
public abstract class SoeMessage implements Message {

    protected transient boolean compressed = true;
    protected final byte zeroByte;
    protected final UdpPacketType packetType;

    protected transient final ByteBuffer buffer;

    public SoeMessage(UdpPacketType packetType) {
        buffer = ByteBuffer.allocate(496).order(ByteOrder.BIG_ENDIAN);

        this.zeroByte = 0;
        this.packetType = packetType;

        buffer.put(zeroByte);
        packetType.writeToBuffer(buffer);
    }

    public ByteBuffer slice() {
        buffer.limit(buffer.position());
        buffer.rewind();
        return buffer.slice();
    }

    public int size() {
        return buffer.limit();
    }

    public int position() {
        return buffer.position();
    }
}
