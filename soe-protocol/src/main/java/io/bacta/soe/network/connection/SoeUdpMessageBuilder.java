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

package io.bacta.soe.network.connection;

import io.bacta.soe.config.SoeNetworkConfiguration;
import io.bacta.soe.network.message.MultiMessage;
import io.bacta.soe.util.SoeMessageUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Kyle on 3/28/14.
 */
@Slf4j
public class SoeUdpMessageBuilder implements UdpMessageBuilder<ByteBuffer> {

    private final Queue<ByteBuffer> bufferList;
    private final SoeNetworkConfiguration configuration;
    private MultiMessage pendingMulti;
    private ByteBuffer pendingBuffer;

    public SoeUdpMessageBuilder(final SoeNetworkConfiguration configuration) {
        this.configuration = configuration;
        bufferList = new ArrayBlockingQueue<>(configuration.getMaxOutstandingPackets());
        pendingMulti = null;
        pendingBuffer = null;
    }

    @Override
    public synchronized boolean add(final ByteBuffer buffer) {

        if(!configuration.isMultiSoeMessages()) {
            return bufferList.add(buffer);
        }

        LOGGER.trace("Adding: {}", SoeMessageUtil.bytesToHex(buffer));
        LOGGER.trace("Queue Size: {}" ,bufferList.size());

        if(buffer.remaining() > 0xFF) {
            flush();
            LOGGER.trace("Buffer too large for multi: {}", buffer.remaining());
            return bufferList.add(buffer);
        }
        if(pendingMulti == null) {
            if(pendingBuffer == null) {
                pendingBuffer = buffer;
                LOGGER.trace("No data pending, message is first");
            } else {
                if (pendingBuffer.remaining() + buffer.remaining() <= configuration.getMaxReliablePayload()) {
                    pendingMulti = new MultiMessage(pendingBuffer, buffer);
                    LOGGER.trace("Combining: {}", SoeMessageUtil.bytesToHex(pendingBuffer));
                    LOGGER.trace("Combining: {}" ,SoeMessageUtil.bytesToHex(buffer));
                    pendingBuffer = null;
                } else {
                    flush();
                    pendingBuffer = buffer;
                }
            }
        } else {
            if(pendingMulti.position() + buffer.remaining() > configuration.getMaxReliablePayload()) {
                flush();
                pendingBuffer = buffer;
            } else {
                pendingMulti.add(buffer);
                LOGGER.trace("Appending: " + SoeMessageUtil.bytesToHex(buffer));
            }
        }
        return true;
    }

    private void flush() {
        if (pendingMulti != null) {
            ByteBuffer send = pendingMulti.slice();
            bufferList.add(send);
            LOGGER.trace("Flushing pending data: {} {}", send.remaining(), SoeMessageUtil.bytesToHex(send));
            pendingMulti = null;
        }

        if(pendingBuffer != null) {
            bufferList.add(pendingBuffer);
            pendingBuffer = null;
        }
    }

    @Override
    public synchronized ByteBuffer buildNext() {

        ByteBuffer buffer = bufferList.poll();

        if(buffer == null && pendingMulti != null) {
            buffer = pendingMulti.slice();
            pendingMulti = null;
        }
        if(buffer == null) {
            buffer = pendingBuffer;
            pendingBuffer = null;
        }

        return buffer;
    }

    @Override
    public void ack(short reliableSequence) {

    }

    @Override
    public void ackAll(short reliableSequence) {

    }
}
