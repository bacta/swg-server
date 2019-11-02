package io.bacta.soe.ping;

import io.bacta.engine.network.udp.UdpChannel;
import io.bacta.soe.config.ConnectionServerConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

@Component
@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public final class PingTransceiver {

    private final ConnectionServerConfiguration config;
    private final UdpChannel channel;

    @Inject
    public PingTransceiver(final ConnectionServerConfiguration config, final UdpChannel channel) {
        this.config = config;
        this.channel = channel;
    }

    public void start() {
        channel.start("Ping", config.getBindAddress(), config.getBindPingPort(), this::receiveMessage);
        LOGGER.info("PING Transceiver started on /{}:{}",
                config.getBindAddress().getHostAddress(),
                config.getBindPingPort());
    }

    public void stop() {
        channel.stop();
    }

    public void sendMessage(InetSocketAddress inetSocketAddress, ByteBuffer buffer) {
        channel.writeOutgoing(inetSocketAddress, buffer);
    }

    private void receiveMessage(InetSocketAddress inetSocketAddress, ByteBuffer buffer) {
        ByteBuffer pong = ByteBuffer.allocate(4);
        pong.putInt(buffer.getInt());
        pong.rewind();

        sendMessage(inetSocketAddress, pong);
    }
}
