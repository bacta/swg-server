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

package io.bacta.network.udp.netty

import com.codahale.metrics.MetricRegistry
import io.bacta.Application
import io.bacta.network.channel.InboundMessageChannel
import io.bacta.network.udp.UdpConnection
import io.bacta.network.udp.UdpEmitter
import io.bacta.network.udp.UdpMetrics
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

import javax.inject.Inject
import java.nio.ByteBuffer

/**
 * Created by kyle on 4/11/2017.
 */
@SpringBootTest(classes = Application.class)
@TestPropertySource(locations = "/network.properties")
class UdpTransceiverTest extends Specification {

    @Inject
    MetricRegistry metricRegistry;

    def "IsAvailable"() {
        when:

        def receiverMetrics = new UdpMetrics(metricRegistry,"server");
        InboundMessageChannel inboundMessageChannel = Mock()
        def server = new NettyUdpReceiver(InetAddress.localHost, 5000, receiverMetrics, inboundMessageChannel)
        server.start();

        then:
        server.isAvailable()
        server.destroy()
    }

    def "SendReceiveMessage"() {
        setup:

        def metrics = new UdpMetrics(metricRegistry,"server");

        InboundMessageChannel serverInboundMessageChannel = Mock()
        def server = new NettyUdpReceiver(InetAddress.localHost, 5000, metrics, serverInboundMessageChannel)
        server.start();

        UdpEmitter client = server.udpHandler.udpEmitter;

        UdpConnection serverUdpConnection = Mock()
        serverUdpConnection.remoteAddress >> new InetSocketAddress(InetAddress.localHost, 5000)

        when:
        client.sendMessage(serverUdpConnection, ByteBuffer.allocate(1))
        client.sendMessage(serverUdpConnection, ByteBuffer.allocate(1))
        client.sendMessage(serverUdpConnection, ByteBuffer.allocate(1))

        then:

        noExceptionThrown()
        Thread.sleep(1000)
        metrics.sentMessages.count == 3
        metrics.receivedMessages.count == 3
    }
}
