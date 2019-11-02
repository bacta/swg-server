package io.bacta.scene.actor;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.soe.network.handler.GameNetworkMessageHandler;
import io.bacta.soe.network.relay.SwgRequestMessage;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class SceneServerActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), SceneServerActor.class.getSimpleName());
    private final SpringAkkaExtension ext;

    private final GameNetworkMessageHandler gameNetworkMessageHandler;

    @Inject
    public SceneServerActor(final SpringAkkaExtension ext, final GameNetworkMessageHandler gameNetworkMessageHandler) {
        this.ext = ext;
        this.gameNetworkMessageHandler = gameNetworkMessageHandler;
    }

    @Override
    public void preStart() {
        log.info("Scene Server starting");
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SwgRequestMessage.class, this::handleGameNetworkMessage)
                .matchAny(this::unhandledMessage)
                .build();
    }

    private void handleGameNetworkMessage(SwgRequestMessage swgRequestMessage) {
        this.gameNetworkMessageHandler.handle(swgRequestMessage.getContext(), swgRequestMessage.getGameNetworkMessage());
    }

    private void unhandledMessage(Object o) {
        log.info("received unknown message", o);
    }

}
