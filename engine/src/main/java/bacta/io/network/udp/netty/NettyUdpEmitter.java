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

package bacta.io.network.udp.netty;

import bacta.io.network.udp.UdpChannel;
import bacta.io.network.udp.UdpConnection;
import bacta.io.network.udp.UdpEmitter;
import bacta.io.network.udp.UdpMetrics;

import java.nio.ByteBuffer;

/**
 * Created by kyle on 6/11/2017.
 */
class NettyUdpEmitter implements UdpEmitter {

    private final UdpMetrics emitterMetrics;
    private final NettyUdpChannels udpChannels;

    NettyUdpEmitter(final UdpMetrics emitterMetrics) {
        this.emitterMetrics = emitterMetrics;
        this.udpChannels = new NettyUdpChannels();
    }

    @Override
    public void sendMessage(final UdpConnection connection, final ByteBuffer msg) {
        sendMessage(UdpChannel.MAIN, connection, msg);
    }

    @Override
    public void sendMessage(final int channel, final UdpConnection connection,final ByteBuffer msg) {
        emitterMetrics.sendMessage(channel);
        udpChannels.writeToChannel(channel, connection.getRemoteAddress(), msg);
    }

    @Override
    public void registerChannel(final int channel, final UdpChannel udpChannel) {
        emitterMetrics.registerChannel(channel);
        udpChannels.registerChannel(channel, udpChannel);
    }

    boolean hasChannel() {
        return this.udpChannels.hasChannel();
    }
}
