package com.ocdsoft.bacta.swg.protocol.message;

import lombok.Getter;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public final class ReliableNetworkMessage extends SoeMessage implements Comparable<ReliableNetworkMessage> {

    @Getter
    private final int sequenceNumber;

    @Getter
    private int sendAttempts;

    @Getter
    private long lastSendAttempt;

    private final List<ByteBuffer> list = new ArrayList<>();

    private transient boolean finished = false;

    /**
     * This constructor is use for standard reliable messages
     *
     * @param sequenceNumber
     * @param inbuffer
     */
    public ReliableNetworkMessage(short sequenceNumber, ByteBuffer inbuffer) {
        super(UdpPacketType.cUdpPacketReliable1);
        this.sequenceNumber = sequenceNumber;
        buffer.putShort(sequenceNumber);
        list.add(inbuffer);
    }

    /**
     * This constructor is used for reliable fragments
     *
     * @param sequenceNumber
     * @param inbuffer
     */
    public ReliableNetworkMessage(short sequenceNumber, ByteBuffer inbuffer, boolean first, int size) {
        super(UdpPacketType.cUdpPacketFragment1);
        this.sequenceNumber = sequenceNumber;
        buffer.putShort(sequenceNumber);
        if(first) {
            buffer.putInt(size);
        }
        list.add(inbuffer);
    }

    @Override
    public int size() {
        // for 0x19
        int size = 2;

        for (ByteBuffer buffer : list) {
            int sizeCount = (buffer.limit() / 0xFF) - (buffer.limit() % 0xFF == 0 ? 1 : 0) + 1;
            if(sizeCount > 1) {
                sizeCount += 1;
            }
            size += buffer.limit() + sizeCount;
        }
        return size;
    }


    public boolean addMessage(ByteBuffer buffer) {

        if(finished) {
            return false;
        }

        return list.add(buffer);
    }

    public void finish() {

        if(finished) {
            return;
        }

        if (list.size() == 1) {
            buffer.put(list.get(0));
        } else {
            buffer.putShort((short) 0x19);
            for (ByteBuffer listBuffer : list) {
                int byteCount = listBuffer.limit();
                if(byteCount > 0xFF) {
                    int sizeCount = (byteCount / 0xFF) - (byteCount % 0xFF == 0 ? 1 : 0);

                    buffer.put((byte) 0xFF);
                    buffer.put((byte)sizeCount);
                    byteCount -= 0xFF;

                    for (int i = 0; i < sizeCount; ++i) {
                        buffer.put(byteCount > 0xFF ? (byte) 0xFF : (byte) byteCount);
                        byteCount -= 0xFF;
                    }

                } else {
                    buffer.put((byte)byteCount);
                }

                buffer.put(listBuffer);
            }
        }

        finished = true;
    }

    public void addSendAttempt() {
        sendAttempts++;
        lastSendAttempt = System.currentTimeMillis();
    }


    @Override
    public int compareTo(ReliableNetworkMessage o) {
        return getSequenceNumber() - o.getSequenceNumber();
    }
}
