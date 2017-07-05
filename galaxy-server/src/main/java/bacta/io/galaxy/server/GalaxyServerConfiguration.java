/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package bacta.io.galaxy.server;

import com.codahale.metrics.MetricRegistry;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
