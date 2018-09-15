package io.bacta.game.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.game.config.GameServerProperties;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

@Component
@Scope("prototype")
public class ConnectionSupervisor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), ConnectionSupervisor.class.getSimpleName());
    private final SpringAkkaExtension ext;
    private final GameServerProperties properties;
    private ActorRef transRef;
    private ActorRef pingRef;

    @Inject
    public ConnectionSupervisor(final SpringAkkaExtension ext, final GameServerProperties properties) {
        this.ext = ext;
        this.properties = properties;
    }

    @PreDestroy
    private void shutdown() {
        log.info("Sending PoisonPill");
        getSelf().tell(new PoisonPill() {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        }, getSelf());
    }

    @Override
    public void preStart() throws Exception {
        log.info("Starting up");
        transRef = getContext().actorOf(ext.props(GalaxyTransceiverSupervisor.class), "transceiver");
        pingRef = getContext().actorOf(ext.props(PingTransceiverSupervisor.class), "ping");
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
