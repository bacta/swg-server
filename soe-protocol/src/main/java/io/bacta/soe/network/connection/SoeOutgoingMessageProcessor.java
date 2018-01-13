package io.bacta.soe.network.connection;

import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.config.SoeNetworkConfiguration;
import io.bacta.soe.network.message.SoeMessage;
import io.bacta.soe.serialize.GameNetworkMessageSerializer;
import io.bacta.soe.util.SoeMessageUtil;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by kyle on 7/15/2017.
 */

@Slf4j
class SoeOutgoingMessageProcessor {

    private final AtomicLong gameNetworkMessagesSent;
    private final AtomicLong protocolMessagesSent;

    private final AtomicLong clientReliableStamp;

    private final SoeUdpMessageProcessor udpMessageProcessor;
    private final GameNetworkMessageSerializer messageSerializer;

    SoeOutgoingMessageProcessor(final SoeNetworkConfiguration networkConfiguration, final GameNetworkMessageSerializer messageSerializer) {
        this.messageSerializer = messageSerializer;
        udpMessageProcessor = new SoeUdpMessageProcessor(networkConfiguration);

        this.gameNetworkMessagesSent = new AtomicLong();
        this.protocolMessagesSent = new AtomicLong();
        this.clientReliableStamp = new AtomicLong();
    }

    void process(SoeMessage message) {
        udpMessageProcessor.addUnreliable(message.getBuffer());
    }

    boolean process(GameNetworkMessage message) {
        gameNetworkMessagesSent.incrementAndGet();
        ByteBuffer buffer = messageSerializer.writeToBuffer(message);

        return udpMessageProcessor.addReliable(buffer);
    }

    List<ByteBuffer> getPendingMessages() {
        List<ByteBuffer> pendingMessageList = new ArrayList<>();

        ByteBuffer buffer;
        while ((buffer = udpMessageProcessor.processNext()) != null) {
            pendingMessageList.add(buffer);
            protocolMessagesSent.getAndIncrement();
            LOGGER.trace("Sending: {}", SoeMessageUtil.bytesToHex(buffer));
        }

        return pendingMessageList;
    }

    void handleClientAck(short sequenceNum) {
        clientReliableStamp.set(sequenceNum);
        udpMessageProcessor.ack(sequenceNum);
    }

    public void handleClientAckAll(short sequenceNum) {
        clientReliableStamp.set(sequenceNum);
    }

    public long getOutgoingProtocolMessageCount() {
        return protocolMessagesSent.get();
    }
}
