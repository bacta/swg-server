package io.bacta.login.server

import io.bacta.soe.network.connection.SoeConnection
import io.bacta.soe.service.SoeConnectionService
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import javax.inject.Inject

@SpringBootTest(classes = Application.class)
class LoginServerApplicationSpecIT extends Specification {

//    @Inject
//    MetricRegistry metricRegistry;
//
//    @Inject
//    SoeUdpTransceiverGroup soeUdpTransceiverGroup;
//
//    @Inject
//    InboundMessageChannel inboundMessageChannel;
//
    @Inject
    SoeConnectionService connectionService;
//
    static String serverHost = "0.0.0.0"
    static int serverPort = 44453
//
//    void setup() {
//
//        soeUdpTransceiverGroup.registerChannel(SoeUdpChannelBuilder.newBuilder()
//                .withMetricsRegistry(metricRegistry)
//                .withMetricsPrefix("test.client")
//                .withAddress(InetAddress.getByName(serverHost))
//                .withPort(serverPort)
//                .withConnection(SoeConnection.class)
//                .usingInboundChannel(inboundMessageChannel)
//                .build()
//        );
//
//    }
//
//    void cleanup() {
//
//        soeUdpTransceiverGroup.destroy()
//
//    }

    def "TestConnect"() {

        setup:

        SoeConnection connection = (SoeConnection) connectionService.getConnection(InetSocketAddress.createUnresolved(serverHost, serverPort)).get()

        when:

        connection.connect({udpConnection -> "I connected"})

        then:
        noExceptionThrown()
        connection.isConnected()
    }
}
