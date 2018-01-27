package io.bacta.connection.server.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.connection.server.config.ConnectionServerProperties;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.shared.message.SoeTransceiverStart;
import io.bacta.shared.message.SoeTransceiverStarted;
import io.bacta.soe.TransceiverSupervisor;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.InetSocketAddress;

@Component
@Scope("prototype")
public class ConnectionServerManager extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), ConnectionServerManager.class.getSimpleName());
    private final SpringAkkaExtension ext;
    private final ConnectionServerProperties properties;
    private InetSocketAddress transceiverAddress;
    private ActorRef transRef;
    private ActorRef delegateRef;

    @Inject
    public ConnectionServerManager(final SpringAkkaExtension ext, final ConnectionServerProperties properties) {
        this.ext = ext;
        this.properties = properties;
    }

    @Override
    public void preStart() throws Exception {

        log.info("Starting up");
        delegateRef = getContext().actorOf(ext.props(GalaxyDelegate.class), "galaxyDelegate");
        transRef = getContext().actorOf(ext.props(TransceiverSupervisor.class), "transceiverSupervisor");
        transRef.tell(new SoeTransceiverStart(properties.getName(), properties.getBindAddress(), properties.getBindPort()), getSelf());
        super.preStart();
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SoeTransceiverStarted.class, started -> {
                    if(getSender().path().elements().size() <= 3) {
                        this.transceiverAddress = started.getAddress();
                    }
                })
                .match(String.class, s -> {
                    log.info("Received String message: {}", s);
                })
                .matchAny(o -> log.info("received unknown message", o))
                .build();
    }


}
