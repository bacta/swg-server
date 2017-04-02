package com.ocdsoft.bacta.engine.network.io.udp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UdpHandler extends SimpleChannelInboundHandler<DatagramPacket> {

    private final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    private final UdpTransceiver transceiver;

    public UdpHandler(UdpTransceiver transceiver) {
        this.transceiver = transceiver;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        transceiver.setCtx(ctx);
    }

    @Override
    public void channelRead0(ChannelHandlerContext ctx, DatagramPacket msg) {
        transceiver.handleIncoming(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("UdpHandler", cause);
    }
}
