package com.ocdsoft.bacta.soe.protocol.network.connection;

import com.ocdsoft.bacta.engine.network.UdpMessageBuilder;
import com.ocdsoft.bacta.soe.protocol.network.io.udp.NetworkConfiguration;
import com.ocdsoft.bacta.soe.protocol.network.message.MultiMessage;
import com.ocdsoft.bacta.soe.protocol.util.SoeMessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Kyle on 3/28/14.
 */
public class SoeUdpMessageBuilder implements UdpMessageBuilder<ByteBuffer> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SoeUdpMessageBuilder.class);

    private final Queue<ByteBuffer> bufferList;
    private final NetworkConfiguration configuration;
    private MultiMessage pendingMulti;
    private ByteBuffer pendingBuffer;

    public SoeUdpMessageBuilder(final NetworkConfiguration configuration) {
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
                LOGGER.trace("No data pending, com.ocdsoft.bacta.swg.login.message is first");
            } else {
                if (pendingBuffer.remaining() + buffer.remaining() <= configuration.getMaxMultiPayload()) {
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
            if(pendingMulti.position() + buffer.remaining() > configuration.getMaxMultiPayload()) {
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
    public void acknowledge(short sequenceNumber) {

    }
}
