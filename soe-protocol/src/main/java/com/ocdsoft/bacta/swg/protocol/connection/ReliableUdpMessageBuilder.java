package com.ocdsoft.bacta.swg.protocol.connection;

import com.ocdsoft.bacta.engine.network.client.UdpMessageBuilder;
import com.ocdsoft.bacta.swg.protocol.io.udp.NetworkConfiguration;
import com.ocdsoft.bacta.swg.protocol.message.ReliableNetworkMessage;
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

    private final NetworkConfiguration configuration;
    private final AtomicInteger sequenceNum = new AtomicInteger();
    private final Set<ReliableNetworkMessage> containerList;
    private final int maxOutstandingPackets;

    private final SoeUdpConnection connection;

    private ReliableNetworkMessage pendingContainer;

    private final Queue<ReliableNetworkMessage> unacknowledgedQueue;

    public ReliableUdpMessageBuilder(final SoeUdpConnection connection, final NetworkConfiguration configuration) {

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

        // Fragment large message
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
//        for(ReliableNetworkMessage message : unacknowledgedQueue) {
//            if(currentTime - message.getLastSendAttempt() > 10000 + message.getSendAttempts() * (resendDelayPercentage * resendDelayAdjust)) {
//                message.addSendAttempt();
//                return message.getBuffer().slice();
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
