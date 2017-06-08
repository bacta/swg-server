package com.ocdsoft.bacta.engine;

import com.codahale.metrics.MetricRegistry;
import com.ocdsoft.bacta.engine.io.network.channel.InboundMessageChannel;
import com.ocdsoft.bacta.engine.io.network.channel.OutboundMessageChannel;
import com.ocdsoft.bacta.engine.io.network.udp.UdpTransceiver;
import com.ocdsoft.bacta.engine.lang.StringToInetAddress;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;

import javax.inject.Inject;

@SpringBootApplication
@EnableAutoConfiguration
@EnableConfigurationProperties
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public MetricRegistry getMetricsRegistry() {
        return new MetricRegistry();
    }

    @Bean
    public InboundMessageChannel getInboundMessageHandler() {
        return new NoopInboundMessageHandler();
    }

    @Bean
    public OutboundMessageChannel getOutboundMessageHandler() {
        return new NoopOutboundMessageHandler();
    }

    @Bean("conversionService")
    public ConversionService getConversionService() {
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
        conversionService.addConverter(new StringToInetAddress());
        return conversionService;
    }

    @Bean("server")
    @Inject
    public UdpTransceiver getServer(final UdpTransceiver udpTransceiver) {
        return udpTransceiver;
    }

    @Bean("client")
    @Inject
    @Qualifier("client")
    public UdpTransceiver getClient(final UdpTransceiver udpTransceiver) {
        return udpTransceiver;
    }
}
