package io.bacta.soe.message

import io.bacta.soe.Application
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import spock.lang.Specification

@SpringBootTest(classes = [Application.class])
@Profile("test")
class SoeTransceiverSpec extends Specification {

//    @Inject
//    SoeTransceiver soeServer;
//
//    @Inject
//    SoeTransceiver soeClient;
//
//
//    def host = InetAddress.getByName("127.0.0.1")
//    def serverPort = 5000;
//    def clientPort = 5001;
//
//    def "register"() {
//
//        when:
//
//        soeServer.start("test.client", host, serverPort)
//        soeClient.start("test.server", host, clientPort)
//
//        then:
//        noExceptionThrown()
//
//    }
//
//    def "sendAndReceive"() {
//
//    }

}
