package io.bacta.login.server.actor;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.login.server.LoginServerProperties;
import io.bacta.soe.network.message.SwgTerminateMessage;
import io.bacta.soe.network.relay.AkkaGameNetworkMessageRelay;
import io.bacta.soe.network.relay.GameClientMessage;
import io.bacta.soe.network.udp.SoeTransceiver;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class LoginTransceiverActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), LoginTransceiverActor.class.getSimpleName());
    private final SpringAkkaExtension ext;
    private final LoginServerProperties properties;
    private final SoeTransceiver transceiver;

    private final AkkaGameNetworkMessageRelay processor;

    @Inject
    public LoginTransceiverActor(final SpringAkkaExtension ext,
                                 final SoeTransceiver transceiver,
                                 final LoginServerProperties properties,
                                 final AkkaGameNetworkMessageRelay processor) {
        this.ext = ext;
        this.transceiver = transceiver;
        this.processor = processor;
        this.properties = properties;
    }

    @Override
    public void preStart() {
        log.info("Transceiver starting");
        this.processor.setTransceiverRef(getSelf());
        transceiver.start("Login", properties.getBindAddress(), properties.getBindPort());
    }

    @Override
    public void postStop() {
        log.info("Transceiver stopping");
        transceiver.stop();
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
        processor.sendMessage(gameClientMessage.getConnectonId(), gameClientMessage.getMessage());
    }

    private void handleTerminate(SwgTerminateMessage terminateMessage) {

    }

    private void unhandledMessage(Object o) {
        log.info("received unknown message", o);
    }

}
