package io.bacta.login.server

import com.codahale.metrics.MetricFilter
import com.codahale.metrics.MetricRegistry
import groovy.util.logging.Slf4j
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

    static String serverHost = "127.0.0.1"
    static int serverPort = 44453
    static String clientHost = "0.0.0.0"
    static int clientPort = 53173

    void setup() {
        soeClient.start("Client", InetAddress.getByName(clientHost), clientPort)
    }

    void cleanup() {
        soeClient.stop()
        metricRegistry.removeMatching(MetricFilter.ALL)
    }

    def "TestConnect"() {

        setup:

        SoeConnection connection = soeClient.getConnection(new InetSocketAddress(serverHost, serverPort)).get()

        when:

        connection.connect({udpConnection -> log.info("I connected")})
        while(!connection.isConnected()) {
            Thread.sleep(100)
        }

        then:
        Thread.sleep(1000)
        noExceptionThrown()
        connection.isConnected()
    }

    def "TestClientID"() {

        setup:

        SoeConnection connection = soeClient.getConnection(new InetSocketAddress(serverHost, serverPort)).get()

        when:

        connection.connect({ udpConnection -> log.info("I connected")})
        while(!connection.isConnected()) {
            Thread.sleep(100)
        }

        then:
        noExceptionThrown()
        connection.isConnected()
    }
}
