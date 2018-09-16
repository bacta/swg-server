package io.bacta.game.actor.data;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.game.GameServerProperties;
import io.bacta.shared.tre.TreeFile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Optional;

@Component
@Scope("prototype")
public class DatatableSupervisor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), DatatableSupervisor.class.getSimpleName());
    private final SpringAkkaExtension ext;
    private final GameServerProperties properties;
    private final TreeFile treeFile;

    @Inject
    public DatatableSupervisor(final SpringAkkaExtension ext, final GameServerProperties properties, final TreeFile treeFile) {
        this.ext = ext;
        this.properties = properties;
        this.treeFile = treeFile;
    }

    @Override
    public void preStart() throws Exception {
        log.info("Datatable Supervisor starting");
        loadDatatables();
        super.preStart();
    }

    private void loadDatatables() {
        //treeFile
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
