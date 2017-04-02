package com.ocdsoft.bacta.engine.network.client;

/**
 * Created by Kyle on 3/28/14.
 */
public interface UdpMessageBuilder<T> {
    /**
     * Adds this message to be processed
     * @param t message to be processed
     * @return if message was successfully added
     */
    boolean add(T t);

    /**
     * Build the next available message
     * @return Next available message, or null if nothing is available to build
     */
    T buildNext();

    void acknowledge(short sequenceNumber);
}
