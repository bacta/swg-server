package io.bacta.game.actor.galaxy;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.game.actor.node.NodeSceneList;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SceneSupervisor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), SceneSupervisor.class.getSimpleName());

    private final Map<String, ActorRef> sceneMap = new ConcurrentHashMap<>();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(NodeSceneList.class, this::receiveNodeList)
                .matchAny(o -> log.info("received unknown message", o))
                .build();
    }

    private void receiveNodeList(NodeSceneList nodeSceneList) {
        log.debug("Received NodeSceneList from " + getSender());
    }
}
