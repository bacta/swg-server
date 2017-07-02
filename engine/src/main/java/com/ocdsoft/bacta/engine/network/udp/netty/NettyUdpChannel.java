package com.ocdsoft.bacta.engine.network.udp.netty;

import com.ocdsoft.bacta.engine.network.udp.UdpChannel;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by kyle on 6/11/2017.
 */
public class NettyUdpChannel implements UdpChannel {

    private final ChannelHandlerContext ctx;

    public NettyUdpChannel(final ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void writeAndFlush(final InetSocketAddress destination, final ByteBuffer message) {
        DatagramPacket datagramPacket = new DatagramPacket(Unpooled.wrappedBuffer(message), destination);
        ctx.writeAndFlush(datagramPacket);
    }
}
