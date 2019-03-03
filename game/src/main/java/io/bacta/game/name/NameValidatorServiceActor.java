package io.bacta.game.name;

import akka.actor.AbstractActor;

import java.util.HashSet;
import java.util.Set;

public class NameValidatorServiceActor extends AbstractActor {
    /**
     * Name validator does its best to keep track of existing names so that it can validate requests against them;
     * however, the most authoritative check is against the database. This mechanism just allows a request to fail
     * faster. We could get rid of it completely if it becomes too memory intensive. Alternatively, we could
     * make a configuration option that enables or disables its use. If a name isn't contained in this set, then it
     * will ultimately get checked against the database anyways.
     */
    private final Set<String> existingNames = new HashSet<>();

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ValidateName.class, this::validateName)
                .build();
    }

    private void validateName(ValidateName msg) {
    }
}
