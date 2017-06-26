package com.ocdsoft.bacta.engine.io.network.udp;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by kyle on 6/11/2017.
 */
public interface UdpChannels {
    void registerChannel(int channel, UdpChannel udpChannel);
    void writeToChannel(int channel, InetSocketAddress remoteAddress, ByteBuffer msg);
}
