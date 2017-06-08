package com.ocdsoft.bacta.engine.io.network.udp

import com.ocdsoft.bacta.engine.Application
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

import javax.inject.Inject

/**
 * Created by kyle on 4/11/2017.
 */
@SpringBootTest(classes = Application.class)
@TestPropertySource(locations = "/network.properties")
class UdpTransceiverSpec extends Specification {

    @Inject
    UdpTransceiver server;

    @Inject
    UdpTransceiver client;

    def setupSpec() {

    }

    def cleanupSpec() {

    }


    def "IsAvailable"() {
        when:
        server.receiveMessage(_, _)

        then:
        noExceptionThrown()
    }

    def "ReceiveMessage"() {
    }

    def "SendMessage"() {
    }
}
