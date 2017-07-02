package com.ocdsoft.bacta.engine.io.network.udp

import com.codahale.metrics.MetricRegistry
import com.ocdsoft.bacta.engine.Application
import com.ocdsoft.bacta.engine.network.channel.InboundMessageChannel
import com.ocdsoft.bacta.engine.network.udp.UdpEmitterMetrics
import com.ocdsoft.bacta.engine.network.udp.UdpConnection
import com.ocdsoft.bacta.engine.network.udp.UdpEmitter
import com.ocdsoft.bacta.engine.network.udp.UdpReceiverMetrics
import com.ocdsoft.bacta.engine.network.udp.netty.NettyUdpEmitter
import com.ocdsoft.bacta.engine.network.udp.netty.NettyUdpReceiver
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

        def receiverMetrics = new UdpReceiverMetrics(metricRegistry,"server");
        InboundMessageChannel inboundMessageChannel = Mock()
        def server = new NettyUdpReceiver(InetAddress.localHost, 5000, receiverMetrics, inboundMessageChannel)
        server.start();

        then:
        server.isAvailable()
        server.destroy()
    }

    def "SendReceiveMessage"() {
        setup:

        def receiverMetrics = new UdpReceiverMetrics(metricRegistry,"server");

        InboundMessageChannel serverInboundMessageChannel = Mock()
        def server = new NettyUdpReceiver(InetAddress.localHost, 5000, receiverMetrics, serverInboundMessageChannel)
        server.start();

        def emitterMetrics = new UdpEmitterMetrics(metricRegistry, "testclient");
        UdpEmitter client = new NettyUdpEmitter(emitterMetrics, server.getChannel())

        UdpConnection serverUdpConnection = Mock()
        serverUdpConnection.remoteAddress >> new InetSocketAddress(InetAddress.localHost, 5000)

        when:
        client.sendMessage(serverUdpConnection, ByteBuffer.allocate(1))
        client.sendMessage(serverUdpConnection, ByteBuffer.allocate(1))
        client.sendMessage(serverUdpConnection, ByteBuffer.allocate(1))

        then:

        noExceptionThrown()
        Thread.sleep(1000)
        receiverMetrics.getMessageCount() == 3
    }
}
