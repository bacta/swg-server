package com.ocdsoft.bacta.engine.network.io.udp;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;

/**
 * 
 * @author Kyle Burkhardt
 * @since 1.0
 **/
public interface UdpReceiver {
	void receiveMessage(InetSocketAddress client, ByteBuffer msg);
}
