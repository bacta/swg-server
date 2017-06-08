package com.ocdsoft.bacta.engine.io.network.udp;

import com.ocdsoft.bacta.engine.context.ShutdownListener;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * Created by kyle on 4/4/2017.
 */
public interface UdpTransceiver extends UdpReceiver, UdpEmitter, ShutdownListener, Runnable {
    boolean isAvailable();
    void receiveMessage(InetSocketAddress inetSocketAddress, ByteBuffer msg);
    void sendMessage(UdpConnection connection, ByteBuffer msg);
}
