package com.ocdsoft.bacta.engine.network.io.udp;

import com.ocdsoft.bacta.engine.network.UdpConnection;

import java.nio.ByteBuffer;

/**
 * 
 * @author Kyle Burkhardt
 * @since 1.0
 *
 */
public interface UdpEmitter {
	void sendMessage(UdpConnection connection, ByteBuffer msg);
}
