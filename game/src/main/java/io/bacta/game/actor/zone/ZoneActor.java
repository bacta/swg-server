package io.bacta.game.actor.zone;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.game.GameServerProperties;
import io.bacta.game.actor.object.zone.Zone;
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
    private Zone zone;

    @Inject
    public ZoneActor(final SpringAkkaExtension ext, final GameServerProperties properties, final Zone zone) {
        this.ext = ext;
        this.properties = properties;
        this.zone = zone;
    }

    @Override
    public void preStart() throws Exception {
        super.preStart();
        zone.start();
    }

    @Override
    public void preRestart(Throwable reason, Optional<Object> message) throws Exception {
        super.preRestart(reason, message);
        zone.restart();
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
        zone.stop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(GameServerProperties.ZoneServer.class, config -> {
                    zone.configure(config.getName(), config.getIffPath());
                })
                .matchAny(o -> log.info("received unknown message", o))
                .build();
    }
}
