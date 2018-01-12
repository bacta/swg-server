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

package io.bacta.engine.network.udp.netty;

import com.codahale.metrics.MetricRegistry;
import io.bacta.engine.network.channel.InboundMessageChannel;
import io.bacta.engine.network.connection.Connection;
import io.bacta.engine.network.udp.UdpChannel;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by kyle on 6/11/2017.
 */
@Slf4j
public class NettyUdpChannel implements UdpChannel {

    private final InboundMessageChannel messageChannel;
    private final NettyUdpTransceiver udpTransceiver;
    private final Class<? extends Connection> connectionClass;

    public NettyUdpChannel(final InetAddress bindAddress,
                           final int bindPort,
                           final InboundMessageChannel messageChannel,
                           final MetricRegistry metricRegistry,
                           final String metricsName,
                           final Class<? extends Connection> connectionClass) {

        LOGGER.info("Channel Starting for - {}", connectionClass.getSimpleName());
        this.messageChannel = messageChannel;
        this.connectionClass = connectionClass;
        this.udpTransceiver = new NettyUdpTransceiver(
                bindAddress,
                bindPort,
                metricRegistry,
                metricsName,
                this::readIncoming);
    }

    @Override
    public void readIncoming(InetSocketAddress sender, DatagramPacket msg) {
        /// The data comes in as a direct buffer, and we need to
        /// bring it into java space for array access
        ByteBuffer buffer = ByteBuffer.allocate(msg.content().readableBytes());
        msg.content().getBytes(0, buffer);
        buffer.rewind();

        messageChannel.receiveMessage(connectionClass, sender, buffer);
    }

    @Override
    public void writeOutgoing(final InetSocketAddress destination, final ByteBuffer message) {
        udpTransceiver.getEmitter().sendMessage(destination, message);
    }

    @Override
    public void destroy() {
        try {
            LOGGER.info("Channel Shutting down - {}", connectionClass.getSimpleName());
            this.udpTransceiver.stop();
        } catch (Exception e) {
            LOGGER.error("Error stopping channel", e);
        }
    }
}
