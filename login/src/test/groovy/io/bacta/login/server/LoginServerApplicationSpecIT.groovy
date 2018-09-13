package io.bacta.login.server

import com.codahale.metrics.MetricFilter
import com.codahale.metrics.MetricRegistry
import groovy.util.logging.Slf4j
import io.bacta.engine.util.AwaitUtil
import io.bacta.soe.network.connection.ConnectionMap
import io.bacta.soe.network.connection.DefaultConnectionMap
import io.bacta.soe.network.connection.SoeConnection
import io.bacta.soe.network.udp.SoeTransceiver
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import javax.inject.Inject

@Slf4j
@SpringBootTest(classes = Application.class)
class LoginServerApplicationSpecIT extends Specification {

    @Inject
    MetricRegistry metricRegistry;

    @Inject
    @Qualifier("soeTransceiver")
    SoeTransceiver soeClient;

    String serverHost = "127.0.0.1"

    @Inject
    LoginServerProperties properties;

    void setup() {
        soeClient.start("Client")
    }

    void cleanup() {
        soeClient.stop()
        metricRegistry.removeMatching(MetricFilter.ALL)
    }

    def "TestConnect"() {

        setup:

        SoeConnection connection = soeClient.getConnection(new InetSocketAddress(serverHost, properties.getBindPort()));

        when:

        connection.connect({udpConnection -> log.info("I connected")})
        AwaitUtil.awaitTrue(connection.&isConnected, 5)

        then:
        noExceptionThrown()
    }

    def "TestConnect with ConnectionMap"() {

        setup:

        ConnectionMap connectionMap = new DefaultConnectionMap()
        connectionMap.setGetConnectionMethod(soeClient.&getConnection)
        SoeConnection connection = connectionMap.getOrCreate(new InetSocketAddress(serverHost, properties.getBindPort()));

        when:

        connection.connect({udpConnection -> log.info("I connected")})

        then:
        noExceptionThrown()
        AwaitUtil.awaitTrue(connection.&isConnected, 5)
    }

    def "Test Implicit connect"() {

        setup:

        ConnectionMap connectionMap = new DefaultConnectionMap()
        connectionMap.setGetConnectionMethod(soeClient.&getConnection)
        SoeConnection connection = connectionMap.getOrCreate(new InetSocketAddress(serverHost, properties.getBindPort()));
        def startingReceivedReliables = connection.soeUdpConnection.incomingMessageProcessor.gameNetworkMessagesReceived.get()
        def startingSequence = connection.soeUdpConnection.outgoingMessageProcessor.udpMessageProcessor.reliableUdpMessageBuilder.sequenceNum.get()

        when:

        connection.sendMessage(new ImplicitConnectionTestMessage())

        then:
        noExceptionThrown()
        startingReceivedReliables == 0
        startingSequence == 0
        AwaitUtil.awaitTrue(connection.&isConnected, 5)
        connection.soeUdpConnection.outgoingMessageProcessor.gameNetworkMessagesSent.get() == 1
        //connection.soeUdpConnection.incomingMessageProcessor.pendingReliablePackets.pendingMap.size() == 0
        connection.soeUdpConnection.outgoingMessageProcessor.udpMessageProcessor.reliableUdpMessageBuilder.sequenceNum.get() == 1
    }

//    def "Test Broadcast"() {
//
//        setup:
//
//        ConnectionMap connectionMap = new DefaultConnectionMap()
//        connectionMap.setGetConnectionMethod(soeClient.&getConnection)
//        connectionMap.setBroadcastMethod(soeClient.&broadcast)
//        SoeConnection connection = connectionMap.getOrCreate(new InetSocketAddress(serverHost, serverPort));
//
//        when:
//
//        connection.connect({udpConnection -> connectionMap.broadcast(new LoginServerOnline())})
//
//        then:
//        noExceptionThrown()
//        AwaitUtil.awaitTrue(connection.&isConnected, 5)
//    }
}
