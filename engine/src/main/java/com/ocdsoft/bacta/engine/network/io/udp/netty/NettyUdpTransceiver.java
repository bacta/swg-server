package com.ocdsoft.bacta.engine.network.io.udp.netty;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.google.inject.assistedinject.Assisted;
import com.ocdsoft.bacta.engine.network.handler.IncomingMessageHandler;
import com.ocdsoft.bacta.engine.network.handler.OutgoingMessageHandler;
import com.ocdsoft.bacta.engine.network.io.udp.UdpSendChannel;
import com.ocdsoft.bacta.engine.network.io.udp.UdpServer;
import com.ocdsoft.bacta.engine.network.io.udp.UdpTransceiver;
import com.ocdsoft.bacta.engine.network.pipeline.MessagePipeline;
import com.ocdsoft.bacta.engine.network.UdpConnection;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
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
public final class NettyUdpTransceiver implements UdpTransceiver {

    private final UdpServer udpServer;
    private final UdpHandler udpHandler;

    private final IncomingMessageHandler inHandler;

    private final Counter incomingMessages;
    private final Counter outgoingMessages;
    /**
	 * This constructor takes an instance of the client type to be created
     * @param port Port transceiver will listen on
	 * @since 1.0
	 */
    @Inject
	public NettyUdpTransceiver(final MetricRegistry metrics,
                               final UdpSendChannel udpSendChannel,
                               final IncomingMessageHandler inHandler,
                               final OutgoingMessageHandler outHandler,
                               @Assisted final InetAddress bindAddress,
                               @Assisted final int port) {
        incomingMessages = metrics.counter(MetricRegistry.name(NettyUdpTransceiver.class, "incoming-messages"));
        outgoingMessages = metrics.counter(MetricRegistry.name(NettyUdpTransceiver.class, "outgoing-messages"));

        udpSendChannel.setTransceiver(this);
        udpSendChannel.setHandler(outHandler);

        this.inHandler = inHandler;
        this.udpHandler = new UdpHandler();
        udpServer = new NettyUdpServer(bindAddress, port, udpHandler);
	}

    @Override
    public void run() {
        udpServer.run();
    }

    @Override
    public void shutdown() {
        udpServer.stop();
    }

    @Override
    public void receiveMessage(InetSocketAddress inetSocketAddress, ByteBuffer msg) {
        incomingMessages.inc();
        
        inHandler.handleIncoming(inetSocketAddress, msg);
    }

    @Override
    public void sendMessage(final UdpConnection connection, final ByteBuffer msg) {
        DatagramPacket datagramPacket = new DatagramPacket(Unpooled.wrappedBuffer(msg), connection.getRemoteAddress());
        udpHandler.ctx.writeAndFlush(datagramPacket);
        outgoingMessages.inc();
    }

    @Override
    public boolean isAvailable() {
        return udpHandler.ctx != null;
    }

    final class UdpHandler extends SimpleChannelInboundHandler<DatagramPacket> {

        ChannelHandlerContext ctx;

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            this.ctx = ctx;
            super.channelRegistered(ctx);
        }

        @Override
        public void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
            /// The data comes in as a direct buffer, and we need to
            /// bring it into java space for array access
            ByteBuffer buffer = ByteBuffer.allocate(msg.content().readableBytes());
            msg.content().getBytes(0, buffer);
            buffer.rewind();

            receiveMessage(msg.sender(), buffer);
        }

        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            log.error("UdpHandler", cause);
        }
    }
}
