package io.bacta.soe.network.handler;

import io.bacta.soe.network.connection.SoeUdpConnection;

import java.nio.ByteBuffer;

/**
 * Created by kyle on 7/6/2017.
 */
public interface SoeProtocolHandler {
    ByteBuffer handleOutgoing(SoeUdpConnection sender, ByteBuffer buffer);

    void handleIncoming(SoeUdpConnection sender, ByteBuffer buffer);
}
