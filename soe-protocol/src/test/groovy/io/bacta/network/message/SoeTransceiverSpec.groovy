package io.bacta.network.message

import com.codahale.metrics.MetricRegistry
import io.bacta.Application
import io.bacta.engine.network.channel.InboundMessageChannel
import io.bacta.soe.network.connection.ClientConnection
import io.bacta.soe.network.connection.GameServerConnection
import io.bacta.soe.network.udp.SoeUdpChannelBuilder
import io.bacta.soe.network.udp.SoeUdpTransceiverGroup
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import javax.inject.Inject

@SpringBootTest(classes = Application.class)
class SoeTransceiverSpec extends Specification {

    @Inject
    MetricRegistry metricRegistry;

    @Inject
    SoeUdpTransceiverGroup soeUdpTransceiverGroup;

    @Inject
    InboundMessageChannel inboundMessageChannel;

    def host = InetAddress.getByName("127.0.0.1")
    def serverPort = 5000;
    def clientPort = 5001;

    def "register"() {

        when:

        soeUdpTransceiverGroup.registerChannel(SoeUdpChannelBuilder.newBuilder()
                .withMetricsRegistry(metricRegistry)
                .withMetricsPrefix("io.bacta.test.client")
                .withAddress(host)
                .withPort(clientPort)
                .withConnection(ClientConnection.class)
                .usingInboundChannel(inboundMessageChannel)
                .build()
        );

        soeUdpTransceiverGroup.registerChannel(SoeUdpChannelBuilder.newBuilder()
                .withMetricsRegistry(metricRegistry)
                .withMetricsPrefix("io.bacta.test.server")
                .withAddress(host)
                .withPort(serverPort)
                .withConnection(GameServerConnection.class)
                .usingInboundChannel(inboundMessageChannel)
                .build()
        );

        then:
        noExceptionThrown()

    }

    def "sendAndReceive"() {

    }

}
