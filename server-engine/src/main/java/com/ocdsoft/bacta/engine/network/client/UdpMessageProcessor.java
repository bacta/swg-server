package com.ocdsoft.bacta.engine.network.client;

/**
 * Created by Kyle on 3/28/14.
 */
public interface UdpMessageProcessor<T> {
    boolean addUnreliable(T unreliable);
    boolean addReliable(T reliable);
    T processNext();
    void acknowledge(short reliableSequence);
}
