package com.ocdsoft.bacta.soe.network.connection;

/**
 * Created by Kyle on 3/28/14.
 */
public interface UdpMessageBuilder<T> {
    /**
     * Adds message to be processed
     * @param t message to be processed
     * @return if message was successfully added
     */
    boolean add(T t);

    /**
     * Build the next isOpen com.ocdsoft.bacta.swg.login.message
     * @return Next isOpen com.ocdsoft.bacta.swg.login.message, or null if nothing is isOpen to build
     */
    T buildNext();

    void acknowledge(short sequenceNumber);
}
