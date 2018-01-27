package io.bacta.galaxy.server.actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.connection.server.ConnectionServerApplication;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.galaxy.server.config.GalaxyServerProperties;
import io.bacta.shared.message.SoeTransceiverStart;
import io.bacta.soe.TransceiverSupervisor;
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope("prototype")
public class GalaxyManager extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), GalaxyManager.class.getSimpleName());
    private final SpringAkkaExtension ext;
    private final GalaxyServerProperties properties;

    @Inject
    public GalaxyManager(final SpringAkkaExtension ext, final GalaxyServerProperties properties) {
        this.ext = ext;
        this.properties = properties;
    }

    @Override
    public void preStart() throws Exception {

        log.info("Starting up");
        getContext().actorOf(ext.props(ClusterListener.class), "clusterListener");
        ActorRef transRef = getContext().actorOf(ext.props(TransceiverSupervisor.class), "transceiverSupervisor");
        transRef.tell(new SoeTransceiverStart(properties.getGalaxyName(), properties.getBindAddress(), properties.getBindPort()), getSelf());

        if(properties.isConnectionServer()) {
            new SpringApplicationBuilder(ConnectionServerApplication.class)
                    .bannerMode(Banner.Mode.OFF)
                    .logStartupInfo(false)
                    .run();
        }
        super.preStart();
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()

                .match(String.class, s -> {
                    log.info("Received String message: {}", s);
                })
                .matchAny(o -> log.info("received unknown message", o))
                .build();
    }
}
