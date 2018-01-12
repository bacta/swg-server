package io.bacta.soe.network.udp;

import com.codahale.metrics.MetricRegistry;
import io.bacta.engine.network.channel.InboundMessageChannel;
import io.bacta.engine.network.udp.UdpChannel;
import io.bacta.engine.network.udp.netty.NettyUdpChannel;
import io.bacta.soe.network.connection.SoeConnection;

import java.net.InetAddress;

/**
 * Created by kyle on 7/12/2017.
 */
public class SoeUdpChannelBuilder {

    private InetAddress address;
    private int port;
    private InboundMessageChannel<SoeConnection> messageChannel;
    private MetricRegistry metricRegistry;
    private String metricsName;
    private Class<? extends SoeConnection> connectionClass;

    private SoeUdpChannelBuilder() {}

    public static SoeUdpChannelBuilder newBuilder() {
        return new SoeUdpChannelBuilder();
    }

    public SoeUdpChannelBuilder withMetricsRegistry(final MetricRegistry metricsRegistry) {
        this.metricRegistry = metricsRegistry;
        return this;
    }

    public SoeUdpChannelBuilder withAddress(InetAddress address) {
        this.address = address;
        return this;
    }

    public SoeUdpChannelBuilder withPort(int port) {
        this.port = port;
        return this;
    }

    public SoeUdpChannelBuilder withConnection(Class<? extends SoeConnection> connectionClass) {
        this.connectionClass = connectionClass;
        return this;
    }

    public SoeUdpChannelBuilder usingInboundChannel(InboundMessageChannel inboundMessageChannel) {
        this.messageChannel = inboundMessageChannel;
        return this;
    }

    public SoeUdpChannelBuilder withMetricsPrefix(String metricsPrefix) {
        this.metricsName = metricsPrefix;
        return this;
    }

    public UdpChannel build() {
        return new NettyUdpChannel(address,
                port,
                messageChannel,
                metricRegistry,
                metricsName,
                connectionClass);
    }


}
