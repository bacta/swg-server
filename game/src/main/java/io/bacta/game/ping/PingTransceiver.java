package io.bacta.game.ping;


import io.bacta.engine.network.udp.UdpChannel;
import io.bacta.game.config.GameServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

@Component
@Scope("prototype")
@Slf4j
public final class PingTransceiver {

    private final GameServerProperties properties;
    private final UdpChannel channel;

    @Inject
    public PingTransceiver(final GameServerProperties properties, final UdpChannel channel) {
        this.properties = properties;
        this.channel = channel;
    }

    public void start() {
        channel.start("Ping", properties.getBindAddress(), properties.getBindPingPort(), this::receiveMessage);
        LOGGER.info("PING Transceiver started on /{}:{}",
                properties.getBindAddress().getHostAddress(),
                properties.getBindPingPort());
    }

    public void stop() {
        channel.stop();
    }

    public void sendMessage(InetSocketAddress inetSocketAddress, ByteBuffer buffer) {
        channel.writeOutgoing(inetSocketAddress, buffer);
    }

    public void receiveMessage(InetSocketAddress inetSocketAddress, ByteBuffer buffer) {
        ByteBuffer pong = ByteBuffer.allocate(4);
        pong.putInt(buffer.getInt());
        pong.rewind();

        sendMessage(inetSocketAddress, pong);
    }
}
