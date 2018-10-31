package io.bacta.engine.db;

import io.bacta.engine.object.NetworkObject;

/**
 * Created by kburkhardt on 1/23/15.
 */
public interface NetworkObjectDatabaseConnector<O extends NetworkObject> extends AutoCloseable {
    long nextId();

    <T extends O> T get(String key);
    <T extends O> T get(long key);
    <T extends O> void persist(T object);

    void connect();
    void close();
    void seed();
}
