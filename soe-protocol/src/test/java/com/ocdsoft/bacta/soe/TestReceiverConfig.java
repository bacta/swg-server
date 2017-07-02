package com.ocdsoft.bacta.soe;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.ocdsoft.bacta.engine.conf.NetworkConfiguration;
import com.ocdsoft.bacta.engine.network.channel.InboundMessageChannel;
import com.ocdsoft.bacta.engine.network.udp.UdpReceiver;
import com.ocdsoft.bacta.engine.network.udp.UdpReceiverMetrics;
import com.ocdsoft.bacta.engine.network.udp.netty.NettyUdpReceiver;
import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by kyle on 6/28/2017.
 */
@Configuration
public class TestReceiverConfig {

    private final MetricRegistry metricRegistry;

    @Inject
    public TestReceiverConfig(final MetricRegistry metricRegistry) {
        this.metricRegistry = metricRegistry;
    }

    @Bean
    public UdpReceiver getReceiver() {

        final UdpReceiver udpReceiver = new NettyUdpReceiver(
                InetAddress.getLoopbackAddress(),
                5000,
                new UdpReceiverMetrics(metricRegistry, "test"),
                new InboundMessageChannel() {
                    @Override
                    public void receiveMessage(InetSocketAddress sender, ByteBuffer message) {

                    }
                }
        );

        udpReceiver.start();
        return udpReceiver;
    }
}
