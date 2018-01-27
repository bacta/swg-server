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

import io.bacta.engine.network.udp.UdpMetrics;
import io.bacta.engine.network.udp.UdpReceiver;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * 
 * @author Kyle Burkhardt
 * @since 1.0
 *
  */
@Slf4j
public final class NettyUdpReceiver implements UdpReceiver {

    private final Thread thread;

    private final BiConsumer<InetSocketAddress, DatagramPacket> receiveMethod;

    private final UdpMetrics metrics;

    private final NettyUdpHandler udpHandler;
    private final NettyUdpServer udpServer;

	public NettyUdpReceiver(final InetAddress bindAddress,
                            final int bindPort,
                            final UdpMetrics metrics,
                            final Consumer<ChannelHandlerContext> channelHandlerContextConsumer,
                            final BiConsumer<InetSocketAddress, DatagramPacket> receiveMethod) {

        this.metrics = metrics;

        this.udpHandler = new NettyUdpHandler(this::receiveMessage, channelHandlerContextConsumer);
        this.udpServer = new NettyUdpServer(bindAddress, bindPort, udpHandler);
        this.thread = new Thread(udpServer);
        thread.setDaemon(true);
        thread.setName("UdpReceiver-" + metrics.getNamePrefix());
        this.receiveMethod = receiveMethod;

        thread.start();

        int attempts = 0;
        while(!isReady()) {
            try {
                attempts++;
                Thread.sleep(200);

            } catch (InterruptedException e) {
               LOGGER.error("Somehow, this was interrupted");
            }

            if(attempts > 50) {
                throw new RuntimeException("Unable to start");
            }
        }
    }

    @Override
    public void receiveMessage(final InetSocketAddress inetSocketAddress, final DatagramPacket packet) {
        metrics.receiveMessage();
        receiveMethod.accept(inetSocketAddress, packet);
    }

    @Override
    public boolean isReady() {
        return udpHandler.isRegistered();
    }

    public void destroy() throws Exception {
        thread.interrupt();
    }

    public InetSocketAddress getAddress() {
        return udpServer.getAddress();
    }
}
