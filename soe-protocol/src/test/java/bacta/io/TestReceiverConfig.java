package bacta.io;

import bacta.io.network.channel.InboundMessageChannel;
import bacta.io.network.udp.UdpMetrics;
import bacta.io.network.udp.UdpReceiver;
import bacta.io.network.udp.netty.NettyUdpReceiver;
import com.codahale.metrics.MetricRegistry;
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
                new UdpMetrics(metricRegistry, "test"),
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
