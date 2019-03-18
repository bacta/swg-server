package io.bacta.soe.network.connection;

import io.bacta.soe.config.SoeNetworkConfiguration;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by kyle on 7/15/2017.
 */

public class DefaultIncomingMessageProcessor implements IncomingMessageProcessor {

    private final IncomingFragmentContainer incomingFragmentContainer;

    private final AtomicLong protocolMessagesReceived;
    private final AtomicLong gameNetworkMessagesReceived;

    private final AtomicInteger orderedStampLast;

    private final AtomicLong reliableStamp;

    private PendingReliablePackets pendingReliablePackets;

    DefaultIncomingMessageProcessor(final SoeNetworkConfiguration networkConfiguration) {
        this.incomingFragmentContainer = new IncomingFragmentContainer();
        this.protocolMessagesReceived = new AtomicLong();
        this.gameNetworkMessagesReceived = new AtomicLong();
        this.orderedStampLast = new AtomicInteger();
        this.reliableStamp = new AtomicLong();
        this.pendingReliablePackets = new PendingReliablePackets(networkConfiguration);
    }

    @Override
    public ByteBuffer processIncomingProtocol(ByteBuffer message) {
        protocolMessagesReceived.incrementAndGet();
        return message;
    }

    @Override
    public ByteBuffer addIncomingFragment(ByteBuffer buffer) {
        return incomingFragmentContainer.addFragment(buffer);
    }

    @Override
    public long getIncomingProtocolMessageCount() {
        return protocolMessagesReceived.get();
    }

    @Override
    public int getOrderedStampLast() {
        return orderedStampLast.get();
    }

    @Override
    public void setOrderedStampLast(short orderedStampLast) {
        this.orderedStampLast.set(orderedStampLast);
    }

    @Override
    public ByteBuffer nextReliable(long nextReliableStamp) {
        return pendingReliablePackets.getNext(nextReliableStamp);
    }

    @Override
    public long getReliableStamp() {
        return reliableStamp.get();
    }

    @Override
    public void incrementNextReliable() {
        reliableStamp.incrementAndGet();
    }

    @Override
    public void addReliable(long reliableId, ByteBuffer buffer) {
        pendingReliablePackets.add(reliableId, buffer);
    }
}
