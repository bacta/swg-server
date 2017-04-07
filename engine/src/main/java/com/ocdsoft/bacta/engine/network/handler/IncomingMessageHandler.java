package com.ocdsoft.bacta.engine.network.handler;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by kyle on 4/4/2017.
 */
public interface IncomingMessageHandler {
    void handleIncoming(InetSocketAddress sender, ByteBuffer message);
}
