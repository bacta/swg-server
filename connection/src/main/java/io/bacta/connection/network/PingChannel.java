package io.bacta.connection.network;

import io.bacta.connection.config.PingChannelProperties;
import io.bacta.engine.network.udp.UdpChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

@Component
@Slf4j
public class PingChannel {

    private final PingChannelProperties config;
    private final UdpChannel channel;

    @Inject
    public PingChannel(final PingChannelProperties config, final UdpChannel channel) {
        this.config = config;
        this.channel = channel;
    }

    private void start() {
        channel.start("Ping", config.getBindAddress(), config.getBindPort(), this::receiveMessage);
        LOGGER.info("PING Transceiver started on /{}:{}",
                config.getBindAddress().getHostAddress(),
                config.getBindPort());
    }

    public void stop() {
        channel.stop();
    }

    private void sendMessage(InetSocketAddress inetSocketAddress, ByteBuffer buffer) {
        channel.writeOutgoing(inetSocketAddress, buffer);
    }

    private void receiveMessage(InetSocketAddress inetSocketAddress, ByteBuffer buffer) {
        ByteBuffer pong = ByteBuffer.allocate(4);
        pong.putInt(buffer.getInt());
        pong.rewind();

        sendMessage(inetSocketAddress, pong);
    }

    @EventListener
    public void handleOrderCreatedEvent(ApplicationStartedEvent startedEvent) {
       start();
    }
}
