package io.bacta.soe.network.protocol;

import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.message.SoeMessageType;

import java.nio.ByteBuffer;

/**
 * Created by kyle on 7/6/2017.
 */
public interface SoeProtocolHandler {
    ByteBuffer processOutgoing(SoeUdpConnection sender, ByteBuffer buffer);
    ByteBuffer processIncoming(SoeUdpConnection sender, ByteBuffer buffer, SoeMessageType packetType);
}
