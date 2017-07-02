package com.ocdsoft.bacta.engine.network.udp.netty;

import com.ocdsoft.bacta.engine.network.udp.UdpEmitterMetrics;
import com.ocdsoft.bacta.engine.network.udp.UdpChannel;
import com.ocdsoft.bacta.engine.network.udp.UdpConnection;
import com.ocdsoft.bacta.engine.network.udp.UdpEmitter;

import java.nio.ByteBuffer;

/**
 * Created by kyle on 6/11/2017.
 */
public class NettyUdpEmitter implements UdpEmitter {

    private final UdpEmitterMetrics emitterMetrics;
    private final NettyUdpChannels udpChannels;

    public NettyUdpEmitter(final UdpEmitterMetrics emitterMetrics, final UdpChannel mainChannel) {
        this.emitterMetrics = emitterMetrics;
        this.udpChannels = new NettyUdpChannels();
        registerChannel(UdpChannel.MAIN, mainChannel);
    }

    @Override
    public void sendMessage(final UdpConnection connection, final ByteBuffer msg) {
        sendMessage(UdpChannel.MAIN, connection, msg);
    }

    @Override
    public void sendMessage(final int channel, final UdpConnection connection,final ByteBuffer msg) {
        emitterMetrics.inc(channel);
        udpChannels.writeToChannel(channel, connection.getRemoteAddress(), msg);
    }

    @Override
    public void registerChannel(final int channel, final UdpChannel udpChannel) {
        emitterMetrics.registerChannel(channel);
        udpChannels.registerChannel(channel, udpChannel);
    }
}
