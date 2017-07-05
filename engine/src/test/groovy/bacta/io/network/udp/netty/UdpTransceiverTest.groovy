package bacta.io.network.udp.netty

import bacta.io.Application
import bacta.io.network.channel.InboundMessageChannel
import bacta.io.network.udp.UdpConnection
import bacta.io.network.udp.UdpEmitter
import bacta.io.network.udp.UdpMetrics
import com.codahale.metrics.MetricRegistry
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
