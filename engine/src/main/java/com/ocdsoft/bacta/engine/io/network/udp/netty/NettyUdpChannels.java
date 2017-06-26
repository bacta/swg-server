package com.ocdsoft.bacta.engine.io.network.udp.netty;

import com.ocdsoft.bacta.engine.io.network.udp.UdpChannel;
import com.ocdsoft.bacta.engine.io.network.udp.UdpChannels;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
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
}
