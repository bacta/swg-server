package com.ocdsoft.bacta.engine.network.io.udp;

import java.net.InetAddress;
import java.net.InetSocketAddress;

public abstract class BasicUdpTransceiver extends UdpTransceiver<InetSocketAddress> {

	public BasicUdpTransceiver(InetAddress bindAddress, int port) {
		super(bindAddress, port);
	}
}
