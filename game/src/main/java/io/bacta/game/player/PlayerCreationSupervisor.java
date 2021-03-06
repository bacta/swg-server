package io.bacta.game.player;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.japi.pf.DeciderBuilder;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.game.galaxy.GalaxyTopics;
import io.bacta.game.message.ClientCreateCharacter;
import io.bacta.game.message.ClientCreateCharacterFailed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static akka.actor.SupervisorStrategy.*;


/**
 * Manages sessions to create a new player characters.
 */
@Slf4j
@Component
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class PlayerCreationSupervisor extends AbstractActor {
    private static final int MAX_PENDING_SESSIONS = 200; //Move this to config

    private final SpringAkkaExtension ext;
    private final Map<ActorRef, ActorRef> pendingSessions = new HashMap<>(MAX_PENDING_SESSIONS);
    /**
     * How long before the sessions timeout, in milliseconds.
     */
    private int sessionTimeout;

    @Inject
    public PlayerCreationSupervisor(SpringAkkaExtension ext) {
        this.ext = ext;
        this.sessionTimeout = 1000 * 5 * 60; //TODO: Pull this in from configuration.

        final ActorRef mediator = DistributedPubSub.get(context().system()).mediator();
        mediator.tell(new DistributedPubSubMediator.Subscribe(GalaxyTopics.PLAYER_CREATION, self()), self());
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(0, Duration.ZERO, DeciderBuilder
                .match(PlayerCreationException.class, this::clientCreateCharacterFailed)
                .matchAny(ex -> escalate())
                .build()
        );
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(DistributedPubSubMediator.SubscribeAck.class, this::subscribed)
                .match(ClientCreateCharacter.class, this::clientCreateCharacter)
                .build();
    }

    /**
     * This message signals that the client is attempting to start a new session to create a character.
     * Clients can only manage a single session to create a character at a time; however, there is a
     * timeout threshold that if eclipsed will allow a new session to begin. If the previous session has
     * not timed out, then the incoming session request must be rejected.
     *
     * @param msg
     */
    private void clientCreateCharacter(ClientCreateCharacter msg) {
        final ActorRef client = sender();

        //TODO: We are going to want to replace the key here with account id.
        //If a session doesn't exist, create one.
        if (!pendingSessions.containsKey(sender())) {
            final ActorRef session = context().actorOf(PlayerCreationSessionActor.props(sessionTimeout));
            pendingSessions.put(client, session);
        }

        final ActorRef existingSession = pendingSessions.get(client);
        existingSession.forward(msg, context());

        //TODO: Implement checking how quickly an account is creating characters.
    }

    /**
     * The creation process through an exception that requires the session to be abandoned. We inform the client
     * and kill the session, removing it from the pending sessions map.
     *
     * @param ex The exception that was thrown.
     * @return The directive that specifies what should happen to the session actor.
     */
    private Directive clientCreateCharacterFailed(PlayerCreationException ex) {
        final ActorRef client = ex.getClient();

        final ClientCreateCharacterFailed message = new ClientCreateCharacterFailed(ex.getCharacterName(), ex.getReason());
        client.tell(message, self());

        pendingSessions.remove(client);

        return stop();
    }

    private void subscribed(DistributedPubSubMediator.SubscribeAck msg) {
        LOGGER.trace("Subscribed");
    }
}
