package io.bacta.soe.network.connection;

import java.nio.ByteBuffer;

public interface IncomingMessageProcessor {
    ByteBuffer processIncomingProtocol(ByteBuffer message);

    ByteBuffer addIncomingFragment(ByteBuffer buffer);

    long getIncomingProtocolMessageCount();

    int getOrderedStampLast();

    void setOrderedStampLast(short orderedStampLast);

    ByteBuffer nextReliable(long nextReliableStamp);

    long getReliableStamp();

    void incrementNextReliable();

    void addReliable(long reliableId, ByteBuffer buffer);
}
