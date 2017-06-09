package com.ocdsoft.bacta.engine.io.network.udp

import com.ocdsoft.bacta.engine.Application
import com.ocdsoft.bacta.engine.conf.NetworkConfig
import com.ocdsoft.bacta.engine.io.network.channel.InboundMessageChannel
import com.ocdsoft.bacta.engine.io.network.udp.netty.NettyUdpTransceiver
import org.springframework.boot.actuate.metrics.CounterService
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.TestPropertySource
import spock.lang.Specification

import javax.inject.Inject
import java.nio.ByteBuffer

/**
 * Created by kyle on 4/11/2017.
 */
@SpringBootTest(classes = Application.class)
@TestPropertySource(locations = "/network.properties")
class UdpTransceiverTest extends Specification {

    @Inject
    CounterService counterService;

    def "IsAvailable"() {
        when:
        NetworkConfig networkConfig = Mock()
        networkConfig.getBindAddress() >> InetAddress.localHost
        networkConfig.getBindPort() >> 5000

        InboundMessageChannel inboundMessageChannel = Mock()
        UdpTransceiver server = new NettyUdpTransceiver(networkConfig, counterService, inboundMessageChannel)
        server.start();

        then:
        server.isAvailable()
    }

    def "SendReceiveMessage"() {
        setup:
        NetworkConfig serverNetworkConfig = Mock()
        serverNetworkConfig.getBindAddress() >> InetAddress.localHost
        serverNetworkConfig.getBindPort() >> 5000

        NetworkConfig clientNetworkConfig = Mock()
        clientNetworkConfig.getBindAddress() >> InetAddress.localHost
        clientNetworkConfig.getBindPort() >> 5001

        InboundMessageChannel serverInboundMessageChannel = Mock()
        UdpTransceiver server = new NettyUdpTransceiver(serverNetworkConfig, counterService, serverInboundMessageChannel)
        server.start();

        InboundMessageChannel clientInboundMessageChannel = Mock()
        UdpTransceiver client = new NettyUdpTransceiver(clientNetworkConfig, counterService, clientInboundMessageChannel)
        client.start();

        when:

        then:

    }
}
