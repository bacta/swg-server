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

package io.bacta.network.udp.netty;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.bacta.network.udp.UdpChannel;
import io.bacta.network.udp.UdpChannels;
import org.springframework.util.Assert;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by kyle on 6/11/2017.
 */
class NettyUdpChannels implements UdpChannels {

    private final TIntObjectMap<UdpChannel> channels;

    NettyUdpChannels() {
        channels = new TIntObjectHashMap<>(1);
    }

    @Override
    public void registerChannel(final int channel, final UdpChannel udpChannel) {
        channels.put(channel, udpChannel);
    }

    @Override
    public void writeToChannel(final int channel, final InetSocketAddress remoteAddress, final ByteBuffer msg) {
        UdpChannel udpChannel = channels.get(channel);
        Assert.notNull(udpChannel, "Sending to non-existent channel");

        udpChannel.writeAndFlush(remoteAddress, msg);
    }

    @Override
    public boolean hasChannel() {
        return !channels.isEmpty();
    }
}
