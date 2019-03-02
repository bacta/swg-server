package io.bacta.game.actor.scene;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.game.GameServerProperties;
import io.bacta.game.actor.object.scene.Scene;
import io.bacta.soe.network.forwarder.SwgRequestMessage;
import io.bacta.soe.network.handler.GameNetworkMessageHandler;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;

@Component
@Scope("prototype")
public class SceneActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), SceneActor.class.getSimpleName());
    private final SpringAkkaExtension ext;
    private final GameServerProperties properties;
    private final GameNetworkMessageHandler dispatcher;
    private final Scene scene;

    @Inject
    public SceneActor(final SpringAkkaExtension ext,
                      final GameServerProperties properties,
                      final GameNetworkMessageHandler dispatcher,
                      final Scene scene) {
        this.ext = ext;
        this.properties = properties;
        this.dispatcher = dispatcher;
        this.scene = scene;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        scene.start();
    }

    @Override
    public void preRestart(Throwable reason, Optional<Object> message) throws Exception {
        super.preRestart(reason, message);
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        scene.stop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GameServerProperties.Scene.class, this::configure)
                .match(SwgRequestMessage.class, this::handleRequest)
                .matchAny(o -> log.info("received unknown message", o))
                .build();
    }

    private void configure(GameServerProperties.Scene config) {
        scene.configure(config.getName(), config.getIffPath());
        scene.restart();
    }

    private void handleRequest(SwgRequestMessage requestMessage) {
        dispatcher.dispatch(requestMessage.getZeroByte(), requestMessage.getOpcode(), requestMessage.getRemoteAddress(), requestMessage.getBuffer());
    }
}
