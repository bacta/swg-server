package io.bacta.game.galaxy;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SceneSupervisor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), SceneSupervisor.class.getSimpleName());

    private final Map<String, ActorRef> sceneMap = new ConcurrentHashMap<>();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchAny(o -> log.info("received unknown message", o))
                .build();
    }

}
