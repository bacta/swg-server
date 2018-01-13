package io.bacta.engine.network.connection;

/**
 * Created by kyle on 7/14/2017.
 */
public interface Connection {
    ConnectionState getState();
    void setState(ConnectionState state);
}
