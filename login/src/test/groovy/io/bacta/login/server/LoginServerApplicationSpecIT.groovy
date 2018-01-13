package io.bacta.login.server

import io.bacta.soe.network.connection.SoeConnection
import io.bacta.soe.network.udp.SoeTransceiver
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import javax.inject.Inject

@SpringBootTest(classes = Application.class)
class LoginServerApplicationSpecIT extends Specification {

    @Inject
    @Qualifier("soeTransceiver")
    SoeTransceiver soeClient;

    static String serverHost = "0.0.0.0"
    static int serverPort = 44453
    static String clientHost = "0.0.0.0"
    static int clientPort = 44477

    void setup() {
        soeClient.start("test.client", InetAddress.getByName(clientHost), clientPort)
    }

    void cleanup() {
        soeClient.stop()
    }

    def "TestConnect"() {

        setup:

        SoeConnection connection = soeClient.getConnection(InetSocketAddress.createUnresolved(serverHost, serverPort)).get()

        when:

        connection.connect({udpConnection -> "I connected"})

        then:
        noExceptionThrown()
        connection.isConnected()
    }
}
