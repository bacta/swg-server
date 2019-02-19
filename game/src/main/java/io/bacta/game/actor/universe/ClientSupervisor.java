package io.bacta.game.actor.universe;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class ClientSupervisor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), ClientSupervisor.class.getSimpleName());

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchAny(this::unhandledMessage)
                .build();
    }

    private void unhandledMessage(Object o) {
        log.info("received unknown message", o);
    }
}
