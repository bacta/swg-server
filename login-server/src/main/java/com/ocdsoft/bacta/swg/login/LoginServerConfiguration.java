package com.ocdsoft.bacta.swg.login;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.ocdsoft.bacta.engine.conf.BactaConfiguration;
import com.ocdsoft.bacta.engine.conf.NetworkConfigurationImpl;
import com.ocdsoft.bacta.engine.network.udp.UdpReceiver;
import com.ocdsoft.bacta.engine.network.udp.UdpReceiverMetrics;
import com.ocdsoft.bacta.engine.network.udp.netty.NettyUdpReceiver;
import com.ocdsoft.bacta.soe.config.SoeNetworkConfigurationImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;
import java.net.UnknownHostException;

/**
 * Created by kyle on 4/12/2016.
 */

@Configuration
@ConfigurationProperties
public final class LoginServerConfiguration {

    private ApplicationContext applicationContext;
    private final MetricRegistry metricRegistry;
    private final LoginServerProperties loginServerProperties;

    @Inject
    public LoginServerConfiguration(final MetricRegistry metricRegistry, final LoginServerProperties loginServerProperties) throws UnknownHostException {
        this.metricRegistry = metricRegistry;
        this.loginServerProperties = loginServerProperties;
    }

    @Bean
    @Qualifier("LoginReceiver")
    public UdpReceiver getLoginReceiver() {

        Counter counter = metricRegistry.counter("network.udp.login.messages.incoming");

        final UdpReceiver udpReceiver = new NettyUdpReceiver(
                loginServerProperties.getBindAddress(),
                loginServerProperties.getBindPort(),
                new UdpReceiverMetrics(metricRegistry, "soe.server.connection.login"),
                new ConnectionRelayChannel()
        );

        udpReceiver.start();
        return udpReceiver;
    }
}
