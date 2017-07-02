package com.ocdsoft.bacta.engine.network.udp.netty;

import com.ocdsoft.bacta.engine.network.udp.UdpChannel;
import com.ocdsoft.bacta.engine.network.udp.UdpEmitter;
import com.ocdsoft.bacta.engine.network.udp.UdpReceiver;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;

/**
 * Created by kyle on 6/6/2017.
 */
@Slf4j
public final class NettyUdpHandler extends SimpleChannelInboundHandler<DatagramPacket>  {

    private final UdpReceiver udpReceiver;
    private NettyUdpChannel nettyUdpChannel;

    public NettyUdpHandler(final UdpReceiver nettyUdpReceiver) {
        this.udpReceiver = nettyUdpReceiver;
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.nettyUdpChannel = new NettyUdpChannel(ctx);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
        /// The data comes in as a direct buffer, and we need to
        /// bring it into java space for array access
        ByteBuffer buffer = ByteBuffer.allocate(msg.content().readableBytes());
        msg.content().getBytes(0, buffer);
        buffer.rewind();

        udpReceiver.receiveMessage(msg.sender(), buffer);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("NettyUdpHandler", cause);
    }

    public UdpChannel getChannel() {
        return nettyUdpChannel;
    }
}
