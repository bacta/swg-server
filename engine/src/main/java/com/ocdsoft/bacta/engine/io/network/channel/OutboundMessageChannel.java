package com.ocdsoft.bacta.engine.io.network.channel;

import com.ocdsoft.bacta.engine.io.network.udp.UdpConnection;

import java.nio.ByteBuffer;

/**
 * Created by kyle on 4/4/2017.
 */
public interface OutboundMessageChannel {
    void sendMessage(UdpConnection sender, ByteBuffer message);
}
