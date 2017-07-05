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

package bacta.io.soe.network.connection;

import bacta.io.soe.config.SoeNetworkConfiguration;
import bacta.io.soe.network.message.ReliableNetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Kyle on 3/26/14.
 */

/**
 struct UdpManager::ReliableConfig
 {
     int maxOutstandingBytes;
     int maxOutstandingPackets;
     int maxInstandingPackets;
     int fragmentSize;
     int trickleSize;
     int trickleRate;
     int resendDelayAdjust;
     int resendDelayPercent;
     int resendDelayCap;
     int congestionWindowMinimum;
     bool outOfOrder;
     bool processOnSend;
     bool coalesce;
     bool ackDeduping;
 };
 */
public class ReliableUdpMessageBuilder implements UdpMessageBuilder<ByteBuffer> {

    public final static Logger logger = LoggerFactory.getLogger(ReliableUdpMessageBuilder.class);

    private final SoeNetworkConfiguration configuration;
    private final AtomicInteger sequenceNum = new AtomicInteger();
    private final Set<ReliableNetworkMessage> containerList;
    private final int maxOutstandingPackets;

    private final SoeUdpConnection connection;

    private ReliableNetworkMessage pendingContainer;

    private final Queue<ReliableNetworkMessage> unacknowledgedQueue;

    public ReliableUdpMessageBuilder(final SoeUdpConnection connection, final SoeNetworkConfiguration configuration) {

        this.connection = connection;
        this.configuration = configuration;
        
        this.maxOutstandingPackets = configuration.getMaxOutstandingPackets();

        containerList = Collections.synchronizedSet(new TreeSet<ReliableNetworkMessage>());
        pendingContainer = null;

        unacknowledgedQueue = new PriorityBlockingQueue<>(maxOutstandingPackets);
    }

    private short getAndIncrement() {

        int value = sequenceNum.getAndIncrement();
        if(value > Short.MAX_VALUE) {
            value = 0;
            sequenceNum.set(value);
        }

        return (short) value;
    }

    @Override
    public synchronized boolean add(ByteBuffer buffer) {

        if (unacknowledgedQueue.size() >= maxOutstandingPackets) {
            return false;
        }

        if (pendingContainer != null) {
            if (configuration.isMultiGameMessages() && pendingContainer.size() + buffer.limit() + 1 <= configuration.getMaxReliablePayload()) {
                return pendingContainer.addMessage(buffer);
            }
            pendingContainer.finish();
            containerList.add(pendingContainer);
            pendingContainer = null;
        }

        // Fragment large com.ocdsoft.bacta.swg.login.message
        if (buffer.limit() > configuration.getMaxReliablePayload()) {

            if(pendingContainer != null) {
                pendingContainer.finish();
                containerList.add(pendingContainer);
                pendingContainer = null;
            }

            //TODO: Pool instances?
            FragmentProcessor fragmentProcessor = new FragmentProcessor(buffer);
            while (fragmentProcessor.hasNext()) {
                containerList.add(fragmentProcessor.next());
            }

            return true;
        }

        pendingContainer = new ReliableNetworkMessage(getAndIncrement(), buffer);
        return true;
    }

    @Override
    public synchronized ByteBuffer buildNext() {

//
//        for(ReliableNetworkMessage com.ocdsoft.bacta.swg.login.message : unacknowledgedQueue) {
//            if(currentTime - com.ocdsoft.bacta.swg.login.message.getLastSendAttempt() > 10000 + com.ocdsoft.bacta.swg.login.message.getSendAttempts() * (resendDelayPercentage * resendDelayAdjust)) {
//                com.ocdsoft.bacta.swg.login.message.addSendAttempt();
//                return com.ocdsoft.bacta.swg.login.message.getBuffer().slice();
//            }
//        }

        Iterator<ReliableNetworkMessage> iterator = containerList.iterator();

        if (!iterator.hasNext()) {
            if (pendingContainer != null) {
                pendingContainer.finish();
                pendingContainer.addSendAttempt();
                unacknowledgedQueue.add(pendingContainer);
                ByteBuffer slice = pendingContainer.slice();
                pendingContainer = null;
                return slice;
            }
            return null;
        }

        ReliableNetworkMessage message = iterator.next();
        containerList.remove(message);
        message.addSendAttempt();
        unacknowledgedQueue.add(message);
        return message.slice();
    }

    @Override
    public void acknowledge(short sequenceNumber) {
        logger.debug("Client Ack: " + sequenceNumber);

        while (!unacknowledgedQueue.isEmpty() &&
                (unacknowledgedQueue.peek().getSequenceNumber() <= sequenceNumber)) {
            unacknowledgedQueue.poll();
        }

    }

    private class FragmentProcessor {

        private final ByteBuffer buffer;
        private final int size;
        private boolean first;

        FragmentProcessor(ByteBuffer buffer) {
            this.buffer = buffer;
            size = buffer.remaining();
            first = true;
        }

        public boolean hasNext() {
            return buffer.hasRemaining();
        }

        public ReliableNetworkMessage next() {

            int messageSize;
            int maxFragmentPayload = configuration.getMaxReliablePayload();
            if(first) {
                maxFragmentPayload -=4;
            }
            if (buffer.remaining() > maxFragmentPayload) {
                messageSize = maxFragmentPayload;
            } else {
                messageSize = buffer.remaining();
            }

            ByteBuffer slice = buffer.slice();
            slice.limit(messageSize);

            buffer.position(buffer.position() + messageSize);

            ReliableNetworkMessage message = new ReliableNetworkMessage(getAndIncrement(), slice, first, size);
            message.finish();

            first = false;
            return message;
        }
    }
}
