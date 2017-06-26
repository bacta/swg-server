package com.ocdsoft.bacta.soe.protocol.network.connection;

import com.ocdsoft.bacta.engine.io.network.ConnectionState;
import com.ocdsoft.bacta.soe.protocol.network.io.udp.UdpMessageBuilder;
import com.ocdsoft.bacta.soe.protocol.network.io.udp.UdpMessageProcessor;
import com.ocdsoft.bacta.soe.protocol.SharedNetworkConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

/**
 * @author kyle
 */
public final class SoeUdpMessageProcessor implements UdpMessageProcessor<ByteBuffer> {

    private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    private final UdpMessageBuilder<ByteBuffer> udpMessageBuilder;
    private final UdpMessageBuilder<ByteBuffer> reliableUdpMessageBuilder;

    private final SharedNetworkConfiguration configuration;
    private final SoeUdpConnection connection;

    public SoeUdpMessageProcessor(final SoeUdpConnection connection, final SharedNetworkConfiguration configuration) {

        this.connection = connection;
        this.configuration = configuration;

        reliableUdpMessageBuilder = new ReliableUdpMessageBuilder(connection, configuration);
        udpMessageBuilder = new SoeUdpMessageBuilder(configuration);
    }

    @Override
    public boolean addReliable(ByteBuffer buffer) {
        if (buffer == null) throw new NullPointerException();

        return reliableUdpMessageBuilder.add(buffer);
    }

    @Override
    public boolean addUnreliable(ByteBuffer buffer) {
        if (buffer == null) throw new NullPointerException();

        flushReliable();
        return udpMessageBuilder.add(buffer);
    }

    @Override
    public ByteBuffer processNext() {

        flushReliable();
        ByteBuffer message = udpMessageBuilder.buildNext();
        if (message != null && message.remaining() > configuration.getMaxRawPacketSize()) {
            throw new RuntimeException("Sending packet that exceeds " + configuration.getMaxRawPacketSize() + " bytes");
        }
        
        if(message != null) {
            connection.updateLastActivity();
        }
        
        return message;
    }

    @Override
    public void acknowledge(short reliableSequence) {
        if(connection.getState() != ConnectionState.ONLINE) {
            connection.setState(ConnectionState.ONLINE);
        }
        connection.updateLastActivity();
        reliableUdpMessageBuilder.acknowledge(reliableSequence);
    }

    private void flushReliable() {
        ByteBuffer message;
        while ((message = reliableUdpMessageBuilder.buildNext()) != null) {
            udpMessageBuilder.add(message);
        }
    }
}
