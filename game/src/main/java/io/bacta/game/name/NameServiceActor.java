package io.bacta.game.name;

import akka.actor.AbstractActor;
import gnu.trove.map.TObjectLongMap;
import gnu.trove.map.hash.TObjectLongHashMap;
import io.bacta.game.message.ClientRandomNameRequest;
import io.bacta.game.message.ClientRandomNameResponse;
import io.bacta.game.message.ClientVerifyAndLockNameRequest;
import io.bacta.game.message.ClientVerifyAndLockNameResponse;

public class NameServiceActor extends AbstractActor {
    private static final int INITIAL_CAPACITY = 1000;
    private final TObjectLongMap<String> nameToNetworkIdMap = new TObjectLongHashMap<>(INITIAL_CAPACITY);

    @Override
    public void preStart() {
        //On preStart, we probably want to fetch the names from the database as a baseline.
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ClientRandomNameRequest.class, this::clientRandomNameRequest)
                .match(ClientVerifyAndLockNameRequest.class, this::clientVerifyAndLockNameRequest)
                .build();
    }

    private void clientVerifyAndLockNameRequest(ClientVerifyAndLockNameRequest msg) {
        sender().tell(new ClientVerifyAndLockNameResponse(
                msg.getCharacterName(),
                NameErrors.APPROVED
        ), self());
    }

    private void clientRandomNameRequest(ClientRandomNameRequest msg) {
        sender().tell(new ClientRandomNameResponse(
                msg.getCreatureTemplate(),
                "Test Player",
                NameErrors.APPROVED
        ), self());
    }
}
