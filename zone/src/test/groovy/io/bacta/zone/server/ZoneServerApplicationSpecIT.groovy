package io.bacta.zone.server

import com.codahale.metrics.MetricFilter
import com.codahale.metrics.MetricRegistry
import groovy.util.logging.Slf4j
import io.bacta.engine.util.AwaitUtil
import io.bacta.soe.network.connection.SoeConnection
import io.bacta.soe.network.udp.SoeTransceiver
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

import javax.inject.Inject

@Slf4j
@SpringBootTest(classes = Application.class)
class ZoneServerApplicationSpecIT extends Specification {

    @Inject
    MetricRegistry metricRegistry;

    @Inject
    @Qualifier("soeTransceiver")
    SoeTransceiver soeClient;

    String serverHost = "127.0.0.1"
    @Value('${io.bacta.zone.server.bindPort}')
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

        SoeConnection connection = soeClient.getConnection(new InetSocketAddress(serverHost, serverPort)).get()

        when:

        connection.connect({udpConnection -> log.info("I connected")})

        then:
        noExceptionThrown()
        AwaitUtil.awaitTrue(connection.&isConnected, 5)
    }
}
