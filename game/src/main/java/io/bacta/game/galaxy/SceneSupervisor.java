package io.bacta.game.galaxy;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.game.GameServerProperties;
import io.bacta.game.scene.SceneActor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SceneSupervisor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), SceneSupervisor.class.getSimpleName());
    private final SpringAkkaExtension ext;

    private final Map<String, ActorRef> sceneMap = new ConcurrentHashMap<>();
    private final GameServerProperties gameServerProperties;

    @Inject
    private SceneSupervisor(final SpringAkkaExtension ext, final GameServerProperties gameServerProperties) {
        this.gameServerProperties = gameServerProperties;
        this.ext = ext;
    }

    @Override
    public void preStart() {
        log.info("Scene Supervisor Starting");
        startScenes();
    }

    private void startScenes() {
        gameServerProperties.getScenes().forEach(scene -> {
            ActorRef sceneActor = context().actorOf(ext.props(SceneActor.class), scene.getName());
            sceneActor.tell(scene, getSelf());
        });
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchAny(o -> log.info("received unknown message", o))
                .build();
    }

}
