package com.ocdsoft.bacta.engine.io.network.udp.netty;

import com.ocdsoft.bacta.engine.io.network.udp.UdpChannel;
import com.ocdsoft.bacta.engine.io.network.udp.UdpConnection;
import com.ocdsoft.bacta.engine.io.network.udp.UdpEmitter;

import java.nio.ByteBuffer;

/**
 * Created by kyle on 6/11/2017.
 */
class NettyUdpEmitter implements UdpEmitter {

    private final NettyUdpChannels udpChannels;

    NettyUdpEmitter() {
        this.udpChannels = new NettyUdpChannels();
    }

    @Override
    public void sendMessage(final UdpConnection connection, final ByteBuffer msg) {
        sendMessage(0, connection, msg);
    }

    @Override
    public void sendMessage(final int channel, final UdpConnection connection,final ByteBuffer msg) {
        udpChannels.writeToChannel(channel, connection.getRemoteAddress(), msg);
    }

    @Override
    public void registerChannel(final int channel, final UdpChannel udpChannel) {
        udpChannels.registerChannel(channel, udpChannel);
    }
}
