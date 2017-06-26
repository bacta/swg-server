package com.ocdsoft.bacta.engine.io.network.udp;

import com.ocdsoft.bacta.engine.io.network.ConnectionState;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;

public abstract class UdpConnection implements UdpEmitter {
    @Getter
	protected InetSocketAddress remoteAddress;

    @Getter
    @Setter
	protected ConnectionState state;
}
