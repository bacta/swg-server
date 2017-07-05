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
