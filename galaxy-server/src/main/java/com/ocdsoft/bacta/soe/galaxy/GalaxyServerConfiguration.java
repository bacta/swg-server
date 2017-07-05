package com.ocdsoft.bacta.soe.galaxy;

import com.codahale.metrics.MetricRegistry;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

import javax.inject.Inject;

/**
 * Created by kyle on 4/12/2016.
 */

@Configuration
@ConfigurationProperties
public class GalaxyServerConfiguration {

    private final MetricRegistry metricRegistry;
    private final GalaxyServerProperties galaxyServerProperties;

    @Inject
    public GalaxyServerConfiguration(final MetricRegistry metricRegistry,
                                     final GalaxyServerProperties galaxyServerProperties) {
        this.metricRegistry = metricRegistry;
        this.galaxyServerProperties = galaxyServerProperties;
    }

//    @Bean
//    @Inject
//    @Named("PublicReceiver")
//    public UdpReceiver getGalaxyPublicReceiver(final SoeInboundMessageChannel inboundMessageChannel) {
//
//        final UdpReceiver udpReceiver = new NettyUdpReceiver(
//                galaxyServerProperties.getBindAddress(),
//                galaxyServerProperties.getPublicBindPort(),
//                new UdpReceiverMetrics(metricRegistry, "bacta.server.galaxy"),
//                inboundMessageChannel
//        );
//
//        udpReceiver.start();
//        return udpReceiver;
//    }
//
//    @Bean
//    @Inject
//    public SoeMessageService getSendRelay(final SoeNetworkConfiguration networkConfiguration,
//                                          final SoeConnectionCache connectionCache,
//                                          final PublisherService publisherService,
//                                          final MetricRegistry metricRegistry,
//                                          final UdpChannel sendChannel) {
//
//        SoeMessageService relay = new SoeMessageService(
//                networkConfiguration,
//                connectionCache,
//                publisherService,
//                metricRegistry,
//                sendChannel
//        );
//        Thread thread = new Thread(relay);
//        thread.setName("GalaxySend");
//        thread.start();
//
//        return relay;
//    }
}
