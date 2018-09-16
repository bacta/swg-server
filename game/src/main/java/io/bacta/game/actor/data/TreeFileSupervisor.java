package io.bacta.game.actor.data;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.game.GameServerProperties;
import io.bacta.shared.data.SetupSharedFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;

@Component
@Scope("prototype")
public class TreeFileSupervisor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), TreeFileSupervisor.class.getSimpleName());
    private final SpringAkkaExtension ext;
    private final GameServerProperties properties;
    private final SetupSharedFile setupSharedFile;

    @Inject
    public TreeFileSupervisor(final SpringAkkaExtension ext, final GameServerProperties properties, final SetupSharedFile setupSharedFile) {
        this.ext = ext;
        this.properties = properties;
        this.setupSharedFile = setupSharedFile;
    }

    @Override
    public void preStart() throws Exception {

        log.info("TreFile Actor starting");
        super.preStart();
        setupSharedFile.install();
        getContext().actorOf(ext.props(DatatableSupervisor.class), "datatable");
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
