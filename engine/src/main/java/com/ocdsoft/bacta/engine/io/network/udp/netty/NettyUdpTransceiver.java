package com.ocdsoft.bacta.engine.io.network.udp.netty;

import com.ocdsoft.bacta.engine.conf.NetworkConfig;
import com.ocdsoft.bacta.engine.io.network.channel.InboundMessageChannel;
import com.ocdsoft.bacta.engine.io.network.udp.UdpConnection;
import com.ocdsoft.bacta.engine.io.network.udp.UdpTransceiver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * 
 * @author Kyle Burkhardt
 * @since 1.0
 *
  */
@Slf4j
@Scope("prototype")
public final class NettyUdpTransceiver implements UdpTransceiver {

    private final Thread thread;
    private final NettyUdpServer udpServer;
    private final NettyUdpHandler udpHandler;

    private final CounterService counterService;

    private final InboundMessageChannel inboundMessageChannel;

    @Inject
	public NettyUdpTransceiver(final NetworkConfig networkConfiguration,
                               final CounterService counterService,
                               final InboundMessageChannel inboundMessageChannel) {

        this.counterService = counterService;

        this.udpHandler = new NettyUdpHandler(this);
        udpServer = new NettyUdpServer(networkConfiguration.getBindAddress(), networkConfiguration.getBindPort(), udpHandler);
        this.inboundMessageChannel = inboundMessageChannel;
        this.thread = new Thread(udpServer);
        thread.setDaemon(true);
        thread.setName("UdpTranceiver" + networkConfiguration.getBindAddress().getHostAddress() + ":" + networkConfiguration.getBindPort());
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
        counterService.increment("network.udp.messages.incoming");
        inboundMessageChannel.receiveMessage(inetSocketAddress, msg);
    }

    @Override
    public void sendMessage(final UdpConnection connection, final ByteBuffer msg) {
        udpHandler.writeAndFlush(connection, msg);
        counterService.increment("network.udp.messages.outgoing");
    }

    @Override
    public boolean isAvailable() {
        return udpHandler.getCtx() != null;
    }

    @Override
    public void destroy() throws Exception {
        thread.interrupt();
    }
}
