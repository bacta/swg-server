package com.ocdsoft.bacta.soe;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.ocdsoft.bacta.engine.network.udp.UdpReceiverMetrics;
import com.ocdsoft.bacta.soe.network.channel.ConnectionRelayChannel;
import com.ocdsoft.bacta.engine.network.udp.UdpReceiver;
import com.ocdsoft.bacta.engine.network.udp.netty.NettyUdpReceiver;
import lombok.Data;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;


/**
 * Created by kyle on 6/29/2017.
 */

@Configuration
@Data
public class ConnectionServerConfiguration {

    private ApplicationContext applicationContext;
    private final MetricRegistry metricRegistry;
    private final ConnectionServerProperties connectionServerProperties;

    @Inject
    public ConnectionServerConfiguration(final MetricRegistry metricRegistry,
                                         final ConnectionServerProperties connectionServerProperties) {
        this.metricRegistry = metricRegistry;
        this.connectionServerProperties = connectionServerProperties;
    }

    @Bean
    @Qualifier("LoginReceiver")
    public UdpReceiver getLoginReceiver() {

        Counter counter = metricRegistry.counter("network.udp.login.messages.incoming");

        final UdpReceiver udpReceiver = new NettyUdpReceiver(
                connectionServerProperties.getLoginBindAddress(),
                connectionServerProperties.getLoginBindPort(),
                new UdpReceiverMetrics(metricRegistry, "soe.server.connection.login"),
                new ConnectionRelayChannel()
        );

        udpReceiver.start();
        return udpReceiver;
    }

    @Bean
    @Qualifier("GameReceiver")
    public UdpReceiver getGameReceiver() {

        Counter counter = metricRegistry.counter("network.udp.game.messages.incoming");

        final UdpReceiver udpReceiver = new NettyUdpReceiver(
                connectionServerProperties.getGameBindAddress(),
                connectionServerProperties.getGameBindPort(),
                new UdpReceiverMetrics(metricRegistry, "soe.server.connection.game"),
                new ConnectionRelayChannel()
        );

        udpReceiver.start();
        return udpReceiver;
    }
}
