package io.bacta.game.db;

import akka.actor.AbstractActor;

/**
 * Handles communication to a database via messages published to the akka cluster.
 */
public class DatabaseActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return null;
    }
}
