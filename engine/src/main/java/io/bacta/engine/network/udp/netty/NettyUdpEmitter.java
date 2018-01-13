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

import io.bacta.engine.network.udp.UdpEmitter;
import io.bacta.engine.network.udp.UdpMetrics;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by kyle on 6/11/2017.
 */
@Slf4j
class NettyUdpEmitter implements UdpEmitter {

    private final UdpMetrics metrics;
    private ChannelHandlerContext ctx;

    NettyUdpEmitter(final UdpMetrics metrics) {
        this.metrics = metrics;
    }

    void registerContext(ChannelHandlerContext context) {
        this.ctx = context;
    }

    @Override
    public boolean isAvailable() {
        return ctx != null;
    }

    @Override
    public void sendMessage(final InetSocketAddress destination, final ByteBuffer msg) {
        metrics.sendMessage();
        DatagramPacket datagramPacket = new DatagramPacket(Unpooled.wrappedBuffer(msg), destination);
        ChannelFuture future = ctx.writeAndFlush(datagramPacket);
        future.awaitUninterruptibly();
        if(!future.isSuccess()) {
            LOGGER.error("Send error", future.cause());
        }
    }
}
