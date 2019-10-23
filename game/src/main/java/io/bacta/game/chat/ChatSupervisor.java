package io.bacta.game.chat;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ChatSupervisor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), ChatSupervisor.class.getSimpleName());

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
