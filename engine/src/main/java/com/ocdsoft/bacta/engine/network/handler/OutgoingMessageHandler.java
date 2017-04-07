package com.ocdsoft.bacta.engine.network.handler;

import com.ocdsoft.bacta.engine.network.UdpConnection;

import java.nio.ByteBuffer;

/**
 * Created by kyle on 4/4/2017.
 */
public interface OutgoingMessageHandler {
    ByteBuffer handleOutgoing(UdpConnection sender, ByteBuffer message);
}
