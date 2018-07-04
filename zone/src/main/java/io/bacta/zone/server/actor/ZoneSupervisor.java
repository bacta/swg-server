package io.bacta.zone.server.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.ReceiveBuilder;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.engine.utils.SenderUtil;
import io.bacta.game.message.ZoneSupervisorReady;
import io.bacta.shared.message.SoeTransceiverStart;
import io.bacta.shared.message.SoeTransceiverStarted;
import io.bacta.soe.SimpleTransceiverManager;
import io.bacta.zone.server.ZoneServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope("prototype")
public class ZoneSupervisor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), ZoneSupervisor.class.getSimpleName());
    private final SpringAkkaExtension ext;
    private final ZoneServerProperties properties;
    private final ApplicationContext context;
    private ActorRef transRef;

    @Inject
    public ZoneSupervisor(final SpringAkkaExtension ext, final ZoneServerProperties properties, final ApplicationContext context) {
        this.ext = ext;
        this.properties = properties;
        this.context = context;
    }

    @Override
    public void preStart() throws Exception {

        log.info("Starting up");
        transRef = getContext().actorOf(ext.props(SimpleTransceiverManager.class), "transceiverSupervisor");
        transRef.tell(new SoeTransceiverStart(properties.getName(), properties.getBindAddress(), properties.getBindPort()), getSelf());
        super.preStart();
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
    }

    @Override
    public Receive createReceive() {
        return new ReceiveBuilder()
        // When a connection server has registered and is ready to receive traffic
                .match(SoeTransceiverStarted.class, s -> SenderUtil.isPrivileged(getSender()), started -> {
                    ActorSelection selection = getContext().getSystem().actorSelection(started.getAddress().toString() + "/user/galaxyManager");
                    selection.tell(new ZoneSupervisorReady(properties.getName(), started.getAddress()), getContext().self());
                })
                .matchAny(o -> log.info("received unknown message", o))
                .build();
    }
}
