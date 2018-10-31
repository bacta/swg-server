package io.bacta.game.actor.db;

import akka.actor.AbstractActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.game.db.ServerObjectDatabaseConnector;
import io.bacta.game.object.ServerObject;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

/**
 * Supervises actors that save and load data.
 */
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public final class DatabaseSupervisor extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private final SpringAkkaExtension ext;

    private final ServerObjectDatabaseConnector databaseConnector;

    @Inject
    public DatabaseSupervisor(SpringAkkaExtension ext, ServerObjectDatabaseConnector databaseConnector) {
        this.ext = ext;
        this.databaseConnector = databaseConnector;
    }

    @Override
    public void preStart() throws Exception {
        log.info("Database starting");

        databaseConnector.connect();

        super.preStart();
    }

    @Override
    public void postStop() throws Exception {
        log.info("Database stopping");

        databaseConnector.close();

        super.postStop();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PersistObjectMessage.class, this::handlePersistObjectMessage)
                .match(LoadObjectMessage.class, this::handleLoadObjectMessage)
                .build();
    }

    private void handleLoadObjectMessage(LoadObjectMessage msg) {
        //Spawn an actor that will attempt to load the given object in the message.
    }

    private void handlePersistObjectMessage(PersistObjectMessage msg) {
        //TODO: Should we put this on its own actor? Should we queue up a bunch of stuff to write at once?
        this.databaseConnector.persist(msg.object);
    }

    @RequiredArgsConstructor
    public static final class LoadObjectMessage {
        private final long networkId;
    }

    @RequiredArgsConstructor
    public static final class PersistObjectMessage {
        private final ServerObject object;
    }
}
