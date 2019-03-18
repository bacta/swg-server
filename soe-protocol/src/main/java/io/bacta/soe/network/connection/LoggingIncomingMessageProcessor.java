package io.bacta.soe.network.connection;

import java.nio.ByteBuffer;

public class LoggingIncomingMessageProcessor implements IncomingMessageProcessor {

    private final long reliableStamp;

    public LoggingIncomingMessageProcessor(IncomingMessageProcessor incomingMessageProcessor) {
        this.reliableStamp = incomingMessageProcessor.getReliableStamp();
    }

    @Override
    public ByteBuffer processIncomingProtocol(ByteBuffer message) {
        return null;
    }

    @Override
    public ByteBuffer addIncomingFragment(ByteBuffer buffer) {
        return null;
    }

    @Override
    public long getIncomingProtocolMessageCount() {
        return 0;
    }

    @Override
    public int getOrderedStampLast() {
        return 0;
    }

    @Override
    public void setOrderedStampLast(short orderedStampLast) {

    }

    @Override
    public ByteBuffer nextReliable(long nextReliableStamp) {
        return null;
    }

    @Override
    public long getReliableStamp() {
        return reliableStamp;
    }

    @Override
    public void incrementNextReliable() {

    }

    @Override
    public void addReliable(long reliableId, ByteBuffer buffer) {

    }
}
