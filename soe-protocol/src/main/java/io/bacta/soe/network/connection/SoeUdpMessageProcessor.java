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
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * @author kyle
 */
@Slf4j
public final class SoeUdpMessageProcessor implements UdpMessageProcessor<ByteBuffer> {

    private final UdpMessageChannel<ByteBuffer> udpMessageBuilder;
    private final UdpMessageChannel<ByteBuffer> reliableUdpMessageBuilder;

    private final SoeNetworkConfiguration configuration;

    SoeUdpMessageProcessor(final SoeNetworkConfiguration configuration) {

        this.configuration = configuration;

        reliableUdpMessageBuilder = new ReliableMessageChannel(configuration);
        udpMessageBuilder = new UnreliableMessageChannel(configuration);
    }

    @Override
    public boolean addReliable(ByteBuffer buffer) {
        if (buffer == null) throw new NullPointerException();

        return reliableUdpMessageBuilder.add(buffer);
    }

    @Override
    public boolean addUnreliable(ByteBuffer buffer) {
        if (buffer == null) throw new NullPointerException();
        buffer.limit(buffer.position());
        buffer.position(0);

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

        return message;
    }

    @Override
    public void ack(short reliableSequence) {
        reliableUdpMessageBuilder.ack(reliableSequence);
    }

    @Override
    public void ackAll(short reliableSequence) {
        reliableUdpMessageBuilder.ackAll(reliableSequence);
    }

    private void flushReliable() {
        ByteBuffer message;
        while ((message = reliableUdpMessageBuilder.buildNext()) != null) {
            udpMessageBuilder.add(message);
        }
    }
}
