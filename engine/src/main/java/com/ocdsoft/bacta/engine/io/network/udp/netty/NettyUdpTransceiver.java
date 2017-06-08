package com.ocdsoft.bacta.engine.io.network.udp.netty;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.ocdsoft.bacta.engine.conf.NetworkConfig;
import com.ocdsoft.bacta.engine.io.network.udp.UdpConnection;
import com.ocdsoft.bacta.engine.io.network.udp.UdpTransceiver;
import lombok.extern.slf4j.Slf4j;
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
@Component
@Scope("prototype")
public final class NettyUdpTransceiver implements UdpTransceiver {

    private final NettyUdpServer udpServer;
    private final NettyUdpHandler udpHandler;

    private final Counter incomingMessages;
    private final Counter outgoingMessages;

    @Inject
	public NettyUdpTransceiver(final NetworkConfig networkConfiguration,
                               final MetricRegistry metrics) {

        incomingMessages = metrics.counter(MetricRegistry.name(NettyUdpTransceiver.class, "incoming-messages"));
        outgoingMessages = metrics.counter(MetricRegistry.name(NettyUdpTransceiver.class, "outgoing-messages"));

        this.udpHandler = new NettyUdpHandler(this);
        udpServer = new NettyUdpServer(networkConfiguration.getBindAddress(), networkConfiguration.getBindPort(), udpHandler);
    }

    @Override
    public void run() {
        udpServer.run();
    }

    @Override
    public void shutdown() {
        udpServer.shutdown();
    }

    @Override
    public void receiveMessage(final InetSocketAddress inetSocketAddress, final ByteBuffer msg) {
        incomingMessages.inc();
        coreMessageChannel.handleIncoming(inetSocketAddress, msg);
    }

    @Override
    public void sendMessage(final UdpConnection connection, final ByteBuffer msg) {
        udpHandler.writeAndFlush(connection, msg);
        outgoingMessages.inc();
    }

    @Override
    public boolean isAvailable() {
        return udpHandler.getCtx() != null;
    }
}
