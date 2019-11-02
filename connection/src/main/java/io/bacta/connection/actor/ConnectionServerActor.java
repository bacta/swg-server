package io.bacta.connection.actor;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.engine.SpringAkkaExtension;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ConnectionServerActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), ConnectionServerActor.class.getSimpleName());
    private final SpringAkkaExtension ext;

    @Inject
    public ConnectionServerActor(final SpringAkkaExtension ext) {
        this.ext = ext;
    }

    @Override
    public void preStart() {
        log.info("Connection Server starting");

        context().actorOf(ext.props(ConnectionTransceiverActor.class), "transceiver");
    }

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
