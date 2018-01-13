package io.bacta.network.message

import io.bacta.Application
import io.bacta.soe.network.udp.SoeTransceiver
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import javax.inject.Inject

@SpringBootTest(classes = Application.class)
class SoeTransceiverSpec extends Specification {

    @Inject
    SoeTransceiver soeServer;

    @Inject
    SoeTransceiver soeClient;


    def host = InetAddress.getByName("127.0.0.1")
    def serverPort = 5000;
    def clientPort = 5001;

    def "register"() {

        when:

        soeServer.start("test.client", host, serverPort)
        soeClient.start("test.server", host, clientPort)

        then:
        noExceptionThrown()

    }

    def "sendAndReceive"() {

    }

}
