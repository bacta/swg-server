package io.bacta.soe.network.udp;

import io.bacta.engine.network.udp.UdpChannel;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.event.TransceiverStartedEvent;
import io.bacta.soe.event.TransceiverStoppedEvent;
import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.soe.network.connection.SoeConnectionCache;
import io.bacta.soe.network.handler.LoginInboundMessageChannel;
import io.bacta.soe.network.handler.SoeSendHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.inject.Inject;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

/**
 * Created by kyle on 7/12/2017.
 */

@Slf4j
@Component
@Scope("prototype")
public class SoeTransceiver {

    private UdpChannel udpChannel;
    private final LoginInboundMessageChannel inboundMessageChannel;
    private final SoeSendHandler sendHandler;
    private final SoeConnectionCache soeConnectionCache;
    private final ApplicationEventPublisher publisher;

    private String name = "";
    private boolean started;

    @Inject
    public SoeTransceiver(final UdpChannel udpChannel,
                          final LoginInboundMessageChannel inboundMessageChannel,
                          final SoeSendHandler sendHandler,
                          final ApplicationEventPublisher publisher) {

        this.udpChannel = udpChannel;
        this.inboundMessageChannel = inboundMessageChannel;
        this.sendHandler = sendHandler;
        this.soeConnectionCache = inboundMessageChannel.getConnectionCache();
        this.publisher = publisher;
        this.started = false;
    }

    public InetSocketAddress getAddress() {
        return udpChannel.getAddress();
    }

    public void start(final String name) throws UnknownHostException {
        start(name, InetAddress.getByName("0.0.0.0"), 0);
    }

    public void start(final String name, final InetAddress bindAddress, final int bindPort) throws TransceiverAlreadyStartedException {

        if(started) {
            throw new TransceiverAlreadyStartedException();
        }

        this.name = name;
        this.udpChannel.start(name, bindAddress, bindPort, this::receiveMessage);
        this.sendHandler.start(name, soeConnectionCache, inboundMessageChannel.getProtocolHandler(), udpChannel);
        this.started = true;
        publisher.publishEvent(new TransceiverStartedEvent());
    }

    private void receiveMessage(InetSocketAddress sender, ByteBuffer message) {
        inboundMessageChannel.receiveMessage(sender, message);
    }

    public void sendMessage(SoeConnection sender, ByteBuffer message) {
        sendHandler.sendMessage(sender, message);
    }

    public SoeConnection getConnection(final InetSocketAddress address) {
        SoeConnection connection = soeConnectionCache.get(address);
        if(connection == null) {
            connection = inboundMessageChannel.getConnectionProvider().newOutgoingInstance(address);
            soeConnectionCache.put(address, connection);
        }

        return connection;
    }

    public void broadcast(GameNetworkMessage message) {
        soeConnectionCache.broadcast(message);
    }

    @PreDestroy
    public void stop() {
        LOGGER.info("Shutting down SoeTransceiver({})", name);
        udpChannel.stop();
        sendHandler.stop();
        publisher.publishEvent(new TransceiverStoppedEvent());
    }
}
