package io.bacta.game.actor.universe;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import lombok.Getter;

import java.net.InetAddress;

@Getter
public class ClientActor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), ClientActor.class.getSimpleName());

    /**
     * The internet address of the client.
     */
    private InetAddress address;
    /**
     * The current node to which the client is connected.
     */
    private ActorRef node;


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
