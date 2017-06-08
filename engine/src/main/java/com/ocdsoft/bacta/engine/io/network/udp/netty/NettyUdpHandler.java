package com.ocdsoft.bacta.engine.io.network.udp.netty;

import com.ocdsoft.bacta.engine.io.network.udp.UdpConnection;
import io.netty.buffer.Unpooled;
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

    private final NettyUdpTransceiver nettyUdpTransceiver;
    private ChannelHandlerContext ctx;

    public NettyUdpHandler(final NettyUdpTransceiver nettyUdpTransceiver) {
        this.nettyUdpTransceiver = nettyUdpTransceiver;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

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

        nettyUdpTransceiver.receiveMessage(msg.sender(), buffer);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        LOGGER.error("NettyUdpHandler", cause);
    }

    public void writeAndFlush(UdpConnection sender, ByteBuffer message) {
        DatagramPacket datagramPacket = new DatagramPacket(Unpooled.wrappedBuffer(message), sender.getRemoteAddress());
        ctx.writeAndFlush(datagramPacket);
    }
}
