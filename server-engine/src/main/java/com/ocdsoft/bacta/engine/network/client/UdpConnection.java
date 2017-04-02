package com.ocdsoft.bacta.engine.network.client;

import lombok.Getter;

import java.net.InetSocketAddress;

public abstract class UdpConnection {

	@Getter protected InetSocketAddress remoteAddress;

    public abstract void setState(ConnectionState state);
    public abstract ConnectionState getState();
}
