package io.bacta.soe.network.udp;

import io.bacta.engine.network.udp.UdpChannel;
import io.bacta.soe.event.TransceiverStartedEvent;
import io.bacta.soe.event.TransceiverStoppedEvent;
import io.bacta.soe.network.channel.SoeMessageChannel;
import io.bacta.soe.network.channel.SoeSendHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
public class SoeTransceiver {

    private UdpChannel udpChannel;
    private final SoeMessageChannel inboundMessageChannel;
    private final SoeSendHandler sendHandler;
    private final ApplicationEventPublisher publisher;

    private String name = "";
    private boolean started;

    @Inject
    public SoeTransceiver(final UdpChannel udpChannel,
                          final SoeMessageChannel inboundMessageChannel,
                          final SoeSendHandler sendHandler,
                          final ApplicationEventPublisher publisher) {

        this.udpChannel = udpChannel;
        this.inboundMessageChannel = inboundMessageChannel;
        this.sendHandler = sendHandler;
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
        this.sendHandler.start(name, udpChannel);
        this.started = true;

        publisher.publishEvent(new TransceiverStartedEvent());
    }

    private void receiveMessage(InetSocketAddress sender, ByteBuffer message) {
        inboundMessageChannel.receiveMessage(sender, message);
    }

    @PreDestroy
    public void stop() {
        LOGGER.info("Shutting down SoeTransceiver({})", name);
        udpChannel.stop();
        sendHandler.stop();
        publisher.publishEvent(new TransceiverStoppedEvent());
    }
}
