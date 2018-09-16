package io.bacta.game

import com.codahale.metrics.MetricFilter
import com.codahale.metrics.MetricRegistry
import groovy.util.logging.Slf4j
import io.bacta.engine.util.AwaitUtil
import io.bacta.soe.network.connection.SoeConnection
import io.bacta.soe.network.udp.SoeTransceiver
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Profile
import spock.lang.Specification

import javax.inject.Inject

@Slf4j
@SpringBootTest(classes = GameServerTestApplication.class)
@Profile("test")
class GameServerApplicationSpecIT extends Specification {

    @Inject
    MetricRegistry metricRegistry;

    @Inject
    SoeTransceiver soeClient;

    String serverHost = "127.0.0.1"

    @Inject
    GameServerProperties properties;

    void setup() {
        soeClient.start("Client")
    }

    void cleanup() {
        soeClient.stop()
        metricRegistry.removeMatching(MetricFilter.ALL)
    }

    def "TestConnect"() {

        setup:

        SoeConnection connection = soeClient.getConnection(new InetSocketAddress(serverHost, properties.getBindPort()))

        when:

        connection.connect({udpConnection -> log.info("I connected")})
        AwaitUtil.awaitTrue(connection.&isConnected, 5)

        then:
        noExceptionThrown()
    }
}
