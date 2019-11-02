package io.bacta.connection.actor;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.soe.config.ConnectionServerConfiguration;
import io.bacta.soe.network.message.SwgTerminateMessage;
import io.bacta.soe.network.relay.AkkaGameNetworkMessageRelay;
import io.bacta.soe.network.relay.GameClientMessage;
import io.bacta.soe.network.udp.SoeTransceiver;
import io.bacta.soe.ping.PingTransceiver;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ConnectionTransceiverActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), ConnectionTransceiverActor.class.getSimpleName());
    private final SpringAkkaExtension ext;

    private final SoeTransceiver transceiver;
    private final PingTransceiver pingTransceiver;
    private final ConnectionServerConfiguration config;

    private final AkkaGameNetworkMessageRelay processor;

    @Inject
    public ConnectionTransceiverActor(final SpringAkkaExtension ext,
                                      final SoeTransceiver transceiver,
                                      final PingTransceiver pingTransceiver,
                                      final ConnectionServerConfiguration config,
                                      final AkkaGameNetworkMessageRelay processor) {
        this.ext = ext;
        this.transceiver = transceiver;
        this.pingTransceiver = pingTransceiver;
        this.processor = processor;
        this.config = config;
    }

    @Override
    public void preStart() {
        log.info("Transceiver starting");
        this.processor.setTransceiverRef(getSelf());
        transceiver.start("Connection", config.getBindAddress(), config.getBindPort());
        pingTransceiver.start();
    }

    @Override
    public void postStop() {
        log.info("Transceiver stopping");
        transceiver.stop();
        pingTransceiver.stop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GameClientMessage.class, this::handleGameClientMessage)
                .match(SwgTerminateMessage.class, this::handleTerminate)
                .matchAny(this::unhandledMessage)
                .build();
    }

    private void handleGameClientMessage(GameClientMessage gameClientMessage) {

    }

    private void handleTerminate(SwgTerminateMessage terminateMessage) {

    }

    private void unhandledMessage(Object o) {
        log.info("received unknown message", o);
    }

}
