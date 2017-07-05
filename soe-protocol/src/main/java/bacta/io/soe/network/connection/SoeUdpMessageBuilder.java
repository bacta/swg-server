package bacta.io.soe.network.connection;

import bacta.io.soe.config.SoeNetworkConfiguration;
import bacta.io.soe.network.message.MultiMessage;
import bacta.io.soe.util.SoeMessageUtil;
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
                LOGGER.trace("No data pending, com.ocdsoft.bacta.swg.login.message is first");
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
    public void acknowledge(short sequenceNumber) {

    }
}
