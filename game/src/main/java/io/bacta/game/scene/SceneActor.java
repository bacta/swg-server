package io.bacta.game.scene;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.game.GameServerProperties;
import io.bacta.shared.tre.TreeFile;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope("prototype")
public class SceneActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), SceneActor.class.getSimpleName());
    private final SpringAkkaExtension ext;
    private GameServerProperties.Scene config;
    private final Scene scene;

    @Inject
    public SceneActor(final SpringAkkaExtension ext, final ConfigurableApplicationContext context, final TreeFile treeFile) {
        this.ext = ext;
        this.scene = new Scene(context, treeFile);
    }

    @Override
    public void preStart() {
        if(config != null) {
            configure(config);
        }
    }

    @Override
    public void postStop() {
        scene.stop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GameServerProperties.Scene.class, this::configure)
                .matchAny(o -> log.info("received unknown message", o))
                .build();
    }

    private void configure(GameServerProperties.Scene config) {
        this.config = config;
        scene.stop();
        scene.configure(config);
        scene.start();
    }
}
