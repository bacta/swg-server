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
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Kyle on 3/26/14.
 */

/**
 * struct UdpManager::ReliableConfig
 * {
 * int maxOutstandingBytes;
 * int maxOutstandingPackets;
 * int maxInstandingPackets;
 * int fragmentSize;
 * int trickleSize;
 * int trickleRate;
 * int resendDelayAdjust;
 * int resendDelayPercent;
 * int resendDelayCap;
 * int congestionWindowMinimum;
 * bool outOfOrder;
 * bool processOnSend;
 * bool coalesce;
 * bool ackDeduping;
 * };
 */

@Slf4j
public class ReliableMessageChannel implements UdpMessageChannel<ByteBuffer> {

    private final SoeNetworkConfiguration configuration;
    private final AtomicInteger sequenceNum = new AtomicInteger();
    private final int maxOutstandingPackets;

    private final Queue<ReliableNetworkMessage> pendingMessages;
    private final Queue<ByteBuffer> unprocessedBuffers;
    private final Queue<ReliableNetworkMessage> unacknowledgedQueue;

    public ReliableMessageChannel(final SoeNetworkConfiguration configuration) {
        this.configuration = configuration;
        this.maxOutstandingPackets = configuration.getMaxOutstandingPackets();
        pendingMessages =  new PriorityBlockingQueue<>(10);
        unprocessedBuffers = new ArrayBlockingQueue<>(maxOutstandingPackets);
        unacknowledgedQueue = new PriorityBlockingQueue<>(maxOutstandingPackets);
    }

    private short getAndIncrement() {

        int value = sequenceNum.getAndIncrement();
        if (value > Short.MAX_VALUE) {
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

        return unprocessedBuffers.add(buffer);
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

        ReliableNetworkMessage nextMessage = pendingMessages.poll();

        if (nextMessage == null) {
            ByteBuffer buffer = unprocessedBuffers.poll();

            if (buffer != null) {

                // Fragment large messages
                if (buffer.limit() > configuration.getMaxReliablePayload()) {
                    int size = buffer.remaining();
                    List<ByteBuffer> fragments = FragmentUtil.createFragments(buffer, configuration.getMaxReliablePayload());
                    for (int i = 0; i < fragments.size(); i++) {
                        ByteBuffer fragment = fragments.get(i);
                        ReliableNetworkMessage reliable = new ReliableNetworkMessage(
                                getAndIncrement(),
                                fragment,
                                i,
                                size
                        );
                        pendingMessages.add(reliable.finish());
                    }

                    nextMessage = pendingMessages.poll();

                } else {

                    nextMessage = new ReliableNetworkMessage(getAndIncrement(), buffer);
                    if (configuration.isMultiGameMessages()) {
                        while ((buffer = unprocessedBuffers.peek()) != null &&
                                nextMessage.hasRoom(buffer, configuration.getMaxReliablePayload())) {
                            buffer = unprocessedBuffers.poll();
                            nextMessage.addMessage(buffer);
                        }
                    }

                }
            }

            if(nextMessage != null) {
                nextMessage.finish();
                nextMessage.addSendAttempt();
                unacknowledgedQueue.add(nextMessage);
                return nextMessage.slice();
            }

            return null;
        }

        nextMessage.addSendAttempt();
        unacknowledgedQueue.add(nextMessage);
        return nextMessage.slice();
    }

    @Override
    public void ack(short sequenceNumber) {
        LOGGER.debug("Client Ack: " + sequenceNumber);

        Iterator<ReliableNetworkMessage> iter = unacknowledgedQueue.iterator();
        while (iter.hasNext()) {
            ReliableNetworkMessage message = iter.next();

            if (message.getSequenceNumber() == sequenceNumber) {
                unacknowledgedQueue.remove(message);
            }

            if (message.getSequenceNumber() >= sequenceNumber) {
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
