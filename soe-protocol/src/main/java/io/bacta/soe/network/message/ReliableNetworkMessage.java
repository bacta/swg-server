/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.bacta.soe.network.message;

import io.bacta.soe.util.SoeMessageUtil;
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

    private Boolean finished = false;

    /**
     * This constructor is use for standard reliable messages
     *
     * @param sequenceNumber
     * @param inbuffer
     */
    public ReliableNetworkMessage(short sequenceNumber, ByteBuffer inbuffer) {
        super(SoeMessageType.cUdpPacketReliable1);
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
    public ReliableNetworkMessage(short sequenceNumber, ByteBuffer inbuffer, int messagePosition, int totalFragmentSize) {
        super(SoeMessageType.cUdpPacketFragment1);
        this.sequenceNumber = sequenceNumber;
        buffer.putShort(sequenceNumber);
        if(messagePosition == 0) {
            buffer.putInt(totalFragmentSize);
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

    public boolean hasRoom(final ByteBuffer candidate, final int maxSize) {
        return size() + candidate.limit() + 1 <= maxSize;
    }


    public boolean addMessage(ByteBuffer buffer) {

        synchronized (finished) {
            if (finished) {
                return false;
            }
        }

        return list.add(buffer);
    }

    public ReliableNetworkMessage finish() {

        synchronized (finished) {
            if (finished) {
                return this;
            } else {
                finished = true;
            }
        }

        if (list.size() == 1) {
            ByteBuffer message = list.get(0);
            buffer.put(message);
        } else {
            buffer.putShort((short) 0x19);
            for (ByteBuffer listBuffer : list) {
                listBuffer.position(0);
                int byteCount = listBuffer.limit();
                SoeMessageUtil.putVariableValue(buffer, byteCount);
                buffer.put(listBuffer);
            }
        }

        return this;
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
