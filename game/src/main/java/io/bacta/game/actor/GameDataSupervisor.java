package io.bacta.game.actor;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.game.actor.data.TreeFileSupervisor;
import io.bacta.game.config.GameServerProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;

@Component
@Scope("prototype")
public class GameDataSupervisor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), GameDataSupervisor.class.getSimpleName());
    private final SpringAkkaExtension ext;
    private final GameServerProperties properties;

    @Inject
    public GameDataSupervisor(final SpringAkkaExtension ext, final GameServerProperties properties) {
        this.ext = ext;
        this.properties = properties;
    }

    @Override
    public void preStart() throws Exception {

        log.info("Game Data Supervisor starting");
        super.preStart();
        getContext().actorOf(ext.props(TreeFileSupervisor.class), "treFile");
    }

    @Override
    public void preRestart(Throwable reason, Optional<Object> message) throws Exception {
        super.preRestart(reason, message);
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchAny(o -> log.info("received unknown message", o))
                .build();
    }
}
