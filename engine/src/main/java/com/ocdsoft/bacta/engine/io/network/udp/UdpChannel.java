package com.ocdsoft.bacta.engine.io.network.udp;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by kyle on 6/11/2017.
 */
public interface UdpChannel {
    void writeAndFlush(InetSocketAddress destination, ByteBuffer message);
}
