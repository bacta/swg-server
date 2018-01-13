package io.bacta.engine.network.udp.netty;

import com.codahale.metrics.MetricRegistry;
import io.bacta.engine.network.udp.UdpEmitter;
import io.bacta.engine.network.udp.UdpMetrics;
import io.bacta.engine.network.udp.UdpReceiver;
import io.bacta.engine.network.udp.UdpTransceiver;
import io.netty.channel.socket.DatagramPacket;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.function.BiConsumer;

/**
 * Created by kyle on 7/12/2017.
 */
class NettyUdpTransceiver implements UdpTransceiver {

    private final NettyUdpReceiver udpReceiver;
    private final NettyUdpEmitter udpEmitter;
    private final UdpMetrics metrics;

    NettyUdpTransceiver(final InetAddress bindAddress,
                        final int bindPort,
                        final MetricRegistry metricRegistry,
                        final String metricsPrefix,
                        final BiConsumer<InetSocketAddress, DatagramPacket> receiveMethod) {

        this.metrics = new UdpMetrics(metricRegistry, metricsPrefix);

        this.udpEmitter = new NettyUdpEmitter(metrics);
        this.udpReceiver = new NettyUdpReceiver(
                bindAddress,
                bindPort,
                metrics,
                udpEmitter::registerContext,
                receiveMethod);
    }

    @Override
    public UdpReceiver getReceiver() {
        return udpReceiver;
    }

    @Override
    public UdpEmitter getEmitter() {
        return udpEmitter;
    }

    @Override
    public boolean isReady() {
        return udpReceiver.isReady();
    }

    @Override
    public void stop() throws Exception {
        udpReceiver.destroy();
    }
}
