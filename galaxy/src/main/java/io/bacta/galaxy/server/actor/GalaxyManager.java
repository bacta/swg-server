package io.bacta.galaxy.server.actor;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.connection.message.ConnectionServerReady;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.engine.utils.SenderUtil;
import io.bacta.galaxy.server.config.GalaxyServerProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@Scope("prototype")
public class GalaxyManager extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), GalaxyManager.class.getSimpleName());
    private final SpringAkkaExtension ext;
    private final GalaxyServerProperties properties;
    private final ApplicationContext context;

    @Inject
    public GalaxyManager(final SpringAkkaExtension ext, final GalaxyServerProperties properties, final ApplicationContext context) {
        this.ext = ext;
        this.properties = properties;
        this.context = context;
    }

    @Override
    public void preStart() throws Exception {

        log.info("Starting up");
        getContext().actorOf(ext.props(ClusterManager.class), "clusterManager");

        super.preStart();
    }

    @Override
    public void postStop() throws Exception {
        super.postStop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()

                // When a connection server has registered and is ready to receive traffic
                .match(ConnectionServerReady.class, s -> SenderUtil.isPrivileged(getSender()), connReady -> {
                    //this.transceiverAddress = started.getAddress();
                })
                .match(String.class, s -> {
                    log.info("Received String message: {}", s);
                })
                .matchAny(o -> log.info("received unknown message", o))
                .build();
    }
}
