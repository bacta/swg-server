package io.bacta.game.actor;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.game.ping.PingTransceiver;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope("prototype")
public class PingTransceiverSupervisor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), PingTransceiverSupervisor.class.getSimpleName());
    private final PingTransceiver pingTransceiver;

    @Inject
    public PingTransceiverSupervisor(final PingTransceiver pingTransceiver) {
        this.pingTransceiver = pingTransceiver;
    }

    @Override
    public void preStart() throws Exception {
        log.info("Starting Up");
        pingTransceiver.start();
        super.preStart();
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        pingTransceiver.stop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchAny(o -> log.info("received unknown message", o))
                .build();
    }
}
