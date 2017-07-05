package com.ocdsoft.bacta.engine.network.udp;

import com.ocdsoft.bacta.engine.network.ConnectionState;

import java.net.InetSocketAddress;

public interface UdpConnection {
	InetSocketAddress getRemoteAddress();
    ConnectionState getState();
    void setState(ConnectionState state);
}
