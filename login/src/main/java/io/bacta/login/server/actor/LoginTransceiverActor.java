package io.bacta.login.server.actor;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.soe.network.channel.SoeMessageChannel;
import io.bacta.soe.network.message.SwgTerminateMessage;
import io.bacta.soe.network.relay.AkkaGameNetworkMessageRelay;
import io.bacta.soe.network.relay.GameClientMessage;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class LoginTransceiverActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), LoginTransceiverActor.class.getSimpleName());
    private final SpringAkkaExtension ext;

    private final AkkaGameNetworkMessageRelay gameNetworkMessageRelay;

    @Inject
    public LoginTransceiverActor(final SpringAkkaExtension ext,  @Qualifier("ServerChannel") final SoeMessageChannel soeMessageChannel) {
        this.ext = ext;
        this.gameNetworkMessageRelay = (AkkaGameNetworkMessageRelay) soeMessageChannel.getGameNetworkMessageRelay();
    }

    @Override
    public void preStart() {
        log.info("Transceiver starting");
        gameNetworkMessageRelay.setTransceiverRef(getSelf());
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
        gameNetworkMessageRelay.sendMessage(gameClientMessage.getConnectonId(), gameClientMessage.getMessage());
    }

    private void handleTerminate(SwgTerminateMessage terminateMessage) {

    }

    private void unhandledMessage(Object o) {
        log.info("received unknown message", o);
    }

}
