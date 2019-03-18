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
import io.bacta.soe.network.message.ReliableNetworkMessage;
import lombok.extern.slf4j.Slf4j;

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

@Slf4j
public class ReliableMessageChannel implements UdpMessageChannel<ByteBuffer> {

    private final SoeNetworkConfiguration configuration;
    private final AtomicInteger sequenceNum = new AtomicInteger();
    private final Set<ReliableNetworkMessage> containerList;
    private final int maxOutstandingPackets;

    private ReliableNetworkMessage pendingContainer;

    private final Queue<ReliableNetworkMessage> unacknowledgedQueue;

    public ReliableMessageChannel(final SoeNetworkConfiguration configuration) {

        this.configuration = configuration;
        
        this.maxOutstandingPackets = configuration.getMaxOutstandingPackets();

        containerList = Collections.synchronizedSet(new TreeSet<>());
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
    public boolean add(ByteBuffer buffer) {

        // If unacknowledged is full, do not accept any more messages
        if (unacknowledgedQueue.size() >= maxOutstandingPackets) {
            return false;
        }

        // In container has content, add to it
        if (pendingContainer != null) {

            // Add next message if it fits
            if (configuration.isMultiGameMessages() &&
                    pendingContainer.hasRoom(buffer, configuration.getMaxReliablePayload())) {
                return pendingContainer.addMessage(buffer);
            } else {
                // If it doesn't fit, finish existing and add to queue
                pendingContainer.finish();
                containerList.add(pendingContainer);
            }
        }

        // Check for messages larger than max payload
        if (buffer.limit() > configuration.getMaxReliablePayload()) {
            int size = buffer.remaining();
            List<ByteBuffer> fragments = FragmentUtil.createFragments(buffer, configuration.getMaxReliablePayload());
            for(int i = 0; i < fragments.size(); i++) {
                ByteBuffer fragment = fragments.get(i);
                ReliableNetworkMessage reliable = new ReliableNetworkMessage(
                        getAndIncrement(),
                        fragment,
                        i,
                        size
                );
                containerList.add(reliable.finish());
            }
            return true;
        } else {

            // Start new reliable message, increase our sequence number
            pendingContainer = new ReliableNetworkMessage(getAndIncrement(), buffer);
//            if(configuration.isMultiGameMessageSizeLimitToFF() && buffer.limit() >= 0xFF) {
//                containerList.add(pendingContainer.finish());
//                pendingContainer = null;
//            }
            return true;
        }
    }

    @Override
    public ByteBuffer buildNext() {

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
    public void ack(short sequenceNumber) {
        LOGGER.debug("Client Ack: " + sequenceNumber);

        Iterator<ReliableNetworkMessage> iter = unacknowledgedQueue.iterator();
        while(iter.hasNext()) {
            ReliableNetworkMessage message = iter.next();

            if(message.getSequenceNumber() == sequenceNumber) {
                unacknowledgedQueue.remove(message);
            }

            if(message.getSequenceNumber() >= sequenceNumber) {
                break;
            }
        }
    }

    @Override
    public void ackAll(short sequenceNumber) {
        LOGGER.debug("Client AckAll: " + sequenceNumber);

        while (!unacknowledgedQueue.isEmpty() &&
                (unacknowledgedQueue.peek().getSequenceNumber() <= sequenceNumber)) {
            unacknowledgedQueue.poll();
        }

    }
}
