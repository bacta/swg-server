package io.bacta.login.server

import com.codahale.metrics.MetricFilter
import com.codahale.metrics.MetricRegistry
import groovy.util.logging.Slf4j
import io.bacta.engine.util.AwaitUtil
import io.bacta.galaxy.message.GalaxyServerId
import io.bacta.soe.network.connection.ConnectionMap
import io.bacta.soe.network.connection.DefaultConnectionMap
import io.bacta.soe.network.connection.SoeConnection
import io.bacta.soe.network.udp.SoeTransceiver
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import javax.inject.Inject
import java.time.ZonedDateTime

@Slf4j
@SpringBootTest(classes = Application.class)
class LoginServerApplicationSpecIT extends Specification {

    @Inject
    MetricRegistry metricRegistry;

    @Inject
    @Qualifier("soeTransceiver")
    SoeTransceiver soeClient;



    String serverHost = "127.0.0.1"
    @Value('${io.bacta.login.server.bindPort}')
    int serverPort

    void setup() {
        soeClient.start("Client")
    }

    void cleanup() {
        soeClient.stop()
        metricRegistry.removeMatching(MetricFilter.ALL)
    }

    def "TestConnect"() {

        setup:

        SoeConnection connection = soeClient.getConnection(new InetSocketAddress(serverHost, serverPort));

        when:

        connection.connect({udpConnection -> log.info("I connected")})

        then:
        noExceptionThrown()
        AwaitUtil.awaitTrue(connection.&isConnected, 5)
    }

    def "TestConnect with ConnectionMap"() {

        setup:

        ConnectionMap connectionMap = new DefaultConnectionMap()
        connectionMap.setGetConnectionMethod(soeClient.&getConnection)
        SoeConnection connection = connectionMap.getOrCreate(new InetSocketAddress(serverHost, serverPort));

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
        SoeConnection connection = connectionMap.getOrCreate(new InetSocketAddress(serverHost, serverPort));
        def startingReceivedReliables = connection.soeUdpConnection.incomingMessageProcessor.gameNetworkMessagesReceived.get()

        when:

        connection.sendMessage(new GalaxyServerId("test", ZonedDateTime.now().getOffset().getTotalSeconds(), ""))

        then:
        noExceptionThrown()
        startingReceivedReliables == 0
        AwaitUtil.awaitTrue(connection.&isConnected, 5)
        connection.soeUdpConnection.incomingMessageProcessor.gameNetworkMessagesReceived.get() == 1
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
