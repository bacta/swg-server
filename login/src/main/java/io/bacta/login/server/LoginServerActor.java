package io.bacta.login.server;

import akka.actor.AbstractActor;
import akka.actor.ActorSystem;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.bacta.login.server.data.GalaxyRecord;

import javax.inject.Inject;

public final class LoginServerActor extends AbstractActor {

    private final TIntObjectMap<GalaxyRecord> galaxies = new TIntObjectHashMap<>();

    private final ActorSystem actorSystem;

    @Inject
    public LoginServerActor(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }

    @Override
    public Receive createReceive() {
        return null;
    }
}
