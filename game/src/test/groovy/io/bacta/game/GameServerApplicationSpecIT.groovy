package io.bacta.game


import groovy.util.logging.Slf4j
import spock.lang.Specification

@Slf4j
//@SpringBootTest(classes = GameServerTestApplication.class)
//@Profile("test")
class GameServerApplicationSpecIT extends Specification {

//    @Inject
//    MetricRegistry metricRegistry;
//
//    @Inject
//    SoeTransceiver soeClient;
//
//    String serverHost = "127.0.0.1"
//
//    @Inject
//    GameServerProperties properties;
//
//    void setup() {
//        soeClient.start("Client")
//    }
//
//    void cleanup() {
//        soeClient.stop()
//        metricRegistry.removeMatching(MetricFilter.ALL)
//    }

//    def "TestConnect"() {
//
//        setup:
//
//        SoeClient connection = soeClient.getConnection(new InetSocketAddress(serverHost, properties.getBindPort()))
//
//        when:
//
//        connection.connect({udpConnection -> log.info("I connected")})
//        AwaitUtil.awaitTrue(connection.&isConnected, 5)
//
//        then:
//        noExceptionThrown()
//    }
}
