package com.ocdsoft.bacta.engine.network.udp;

import com.ocdsoft.bacta.engine.network.ConnectionState;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;

public abstract class UdpConnection {
    @Getter
	protected InetSocketAddress remoteAddress;

    @Getter
    @Setter
	protected ConnectionState state;
}
