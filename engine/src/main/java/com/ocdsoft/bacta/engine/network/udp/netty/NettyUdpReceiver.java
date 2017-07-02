package com.ocdsoft.bacta.engine.network.udp.netty;

import com.codahale.metrics.Counter;
import com.ocdsoft.bacta.engine.network.channel.InboundMessageChannel;
import com.ocdsoft.bacta.engine.network.udp.UdpChannel;
import com.ocdsoft.bacta.engine.network.udp.UdpReceiver;
import com.ocdsoft.bacta.engine.network.udp.UdpReceiverMetrics;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * 
 * @author Kyle Burkhardt
 * @since 1.0
 *
  */
@Slf4j
public final class NettyUdpReceiver implements UdpReceiver {

    private final Thread thread;
    private final NettyUdpServer udpServer;
    private final NettyUdpHandler udpHandler;

    private final UdpReceiverMetrics metrics;

    private final InboundMessageChannel inboundMessageChannel;

	public NettyUdpReceiver(final InetAddress bindAddress,
                            final int bindPort,
                            final UdpReceiverMetrics metrics,
                            final InboundMessageChannel inboundMessageChannel) {

        this.metrics = metrics;

        this.udpHandler = new NettyUdpHandler(this);
        udpServer = new NettyUdpServer(bindAddress, bindPort, udpHandler);
        this.inboundMessageChannel = inboundMessageChannel;
        this.thread = new Thread(udpServer);
        thread.setDaemon(true);
        thread.setName("UdpReceiver");
    }

    @Override
    public boolean start() {
        thread.start();
        int attempts = 0;
        while(!isAvailable() && attempts < 10) {
            attempts++;
            try {
                Thread.sleep(500);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return isAvailable();
    }

    @Override
    public void receiveMessage(final InetSocketAddress inetSocketAddress, final ByteBuffer msg) {
        metrics.inc();
        inboundMessageChannel.receiveMessage(inetSocketAddress, msg);
    }

    @Override
    public boolean isAvailable() {
        return udpHandler.getChannel() != null;
    }

    @Override
    public UdpChannel getChannel() {
        return udpHandler.getChannel();
    }

    @Override
    public void destroy() throws Exception {
        thread.interrupt();
    }
}
