package io.bacta.connection.server.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.connection.server.config.ConnectionServerProperties;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.engine.utils.SenderUtil;
import io.bacta.shared.message.SoeTransceiverStart;
import io.bacta.shared.message.SoeTransceiverStarted;
import io.bacta.soe.SimpleTransceiverManager;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

@Component
@Scope("prototype")
public class ConnectionServerManager extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), ConnectionServerManager.class.getSimpleName());
    private final SpringAkkaExtension ext;
    private final ConnectionServerProperties properties;
    private ActorRef transRef;
    private ActorRef delegateRef;
    private ActorRef zoneManagerRef;

    @Inject
    public ConnectionServerManager(final SpringAkkaExtension ext, final ConnectionServerProperties properties) {
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
        delegateRef = getContext().actorOf(ext.props(GalaxyDelegate.class), "galaxyDelegate");
        transRef = getContext().actorOf(ext.props(SimpleTransceiverManager.class), "transceiverSupervisor");
        transRef.tell(new SoeTransceiverStart(properties.getName(), properties.getBindAddress(), properties.getBindPort()), getSelf());
        zoneManagerRef = getContext().actorOf(ext.props(ZoneManagerSupervisor.class), "zoneManager");
        super.preStart();
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SoeTransceiverStarted.class, s -> SenderUtil.isPrivileged(getSender()), started -> {
                    delegateRef.tell(started, getSelf());
                })
                .match(String.class, s -> {
                    log.info("Received String message: {}", s);
                })
                .matchAny(o -> log.info("received unknown message", o))
                .build();
    }


}
