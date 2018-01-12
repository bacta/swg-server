package io.bacta.soe.network.handler;

import io.bacta.soe.network.connection.SoeConnection;

import java.nio.ByteBuffer;

/**
 * Created by kyle on 7/6/2017.
 */
public interface SoeProtocolHandler {
    ByteBuffer processOutgoing(SoeConnection sender, ByteBuffer buffer);
    ByteBuffer processIncoming(SoeConnection sender, ByteBuffer buffer);
}
