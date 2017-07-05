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
