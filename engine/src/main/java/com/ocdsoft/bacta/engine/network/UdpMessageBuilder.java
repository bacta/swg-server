package com.ocdsoft.bacta.engine.network;

/**
 * Created by Kyle on 3/28/14.
 */
public interface UdpMessageBuilder<T> {
    /**
     * Adds this com.ocdsoft.bacta.swg.login.message to be processed
     * @param t com.ocdsoft.bacta.swg.login.message to be processed
     * @return if com.ocdsoft.bacta.swg.login.message was successfully added
     */
    boolean add(T t);

    /**
     * Build the next available com.ocdsoft.bacta.swg.login.message
     * @return Next available com.ocdsoft.bacta.swg.login.message, or null if nothing is available to build
     */
    T buildNext();

    void acknowledge(short sequenceNumber);
}
