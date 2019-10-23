package io.bacta.game.db;

import akka.actor.AbstractActor;

/**
 * Persisting a game object requires that the objects baselines and deltas are forwarded to this database actor
 * which will then queue them to be written to the database server. This allows for asynchronous database writes
 * and also allows the database persistence to potentially operate on a separate node, not using resources on the
 * game server.
 *
 * This means that objects aren't immediately persisted. If the database actor were to crash
 */
public class DatabaseActor extends AbstractActor {
    @Override
    public Receive createReceive() {
        return null;
    }
}
