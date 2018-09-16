package io.bacta.game.actor.zone;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.game.GameServerProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;

@Component
@Scope("prototype")
public class ZoneActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), ZoneActor.class.getSimpleName());
    private final SpringAkkaExtension ext;
    private final GameServerProperties properties;

    @Inject
    public ZoneActor(final SpringAkkaExtension ext, final GameServerProperties properties) {
        this.ext = ext;
        this.properties = properties;
    }

    @Override
    public void preStart() throws Exception {
        log.info("Zone starting");
        super.preStart();
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
                .match(GameServerProperties.ZoneServer.class, config -> {
                    log.info("Starting zone {} with iff path {}", config.getName(), config.getIffPath());
                })
                .matchAny(o -> log.info("received unknown message", o))
                .build();
    }
}
