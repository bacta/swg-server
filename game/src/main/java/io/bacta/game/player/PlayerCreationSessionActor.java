package io.bacta.game.player;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.Props;
import io.bacta.actor.ActorConstants;
import io.bacta.game.message.ClientCreateCharacter;
import io.bacta.game.name.NameErrors;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Manages a client's session to create a new player character.
 */
@Getter
@RequiredArgsConstructor
class PlayerCreationSessionActor extends AbstractActor {
    static Props props(long sessionTimeout) {
        return Props.create(PlayerCreationSessionActor.class, () -> new PlayerCreationSessionActor(sessionTimeout));
    }

    private final static String NAME_VALIDATION_SELECTOR = "../../" + ActorConstants.NAME_VALIDATION_SERVICE;

    private final long startTimestamp = System.currentTimeMillis();
    private final long sessionTimeout;

    private ActorSelection nameValidatorService;

    private ClientCreateCharacter createMessage;
    private ActorRef client;
    private boolean started;

    @Override
    public void preStart() {
        this.nameValidatorService = context().actorSelection(NAME_VALIDATION_SELECTOR);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ClientCreateCharacter.class, this::start)
                .build();
    }

    private void start(ClientCreateCharacter msg) throws PlayerCreationException {
        final var client = sender();

        //Any subsequent create character messages we receive cannot succeed if the current session hasn't
        //expired.
        if (started && !isExpired())
            throw new PlayerCreationException(client, msg.getCharacterName(), NameErrors.RETRY);

        //Either it's a brand new session, or its one that has expired and is being restarted.
        this.client = client;
        this.started = true;
        this.createMessage = msg;

        //Validation
        //- Name is not being validated by someone else already (they have a lock on that name)
        //- The template wasn't empty: NO_TEMPLATE
        //- They are authorized for the species chosen: NOT_AUTHORIZED_FOR_SPECIES
        //- Template against creature templates.
        //- Template can be created as an avatar.
        //- Name against static checks. Length, character set, etc.
        //- Name against known existing names.
        //- Name against the database.

        validateObjectTemplate();
        validateCharacterName();

        createCharacter();
    }

    private boolean isExpired() {
        final var currentTimestamp = System.currentTimeMillis();
        return currentTimestamp > startTimestamp + sessionTimeout;
    }

    private void validateObjectTemplate() throws PlayerCreationException {
        final var templateName = createMessage.getTemplateName();

        if (templateName == null || templateName.isEmpty())
            throw new PlayerCreationException(client, createMessage.getCharacterName(), NameErrors.NO_TEMPLATE);
    }

    private void validateCharacterName() throws PlayerCreationException {

    }

    private void createCharacter() {
        //final var creatureObject;
    }
}
