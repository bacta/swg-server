package com.ocdsoft.bacta.engine.io.network.udp;

import java.nio.ByteBuffer;

/**
 * 
 * @author Kyle Burkhardt
 * @since 1.0
 *
 */
public interface UdpEmitter {
	void sendMessage(UdpConnection connection, ByteBuffer msg);
	void sendMessage(int channel, UdpConnection connection, ByteBuffer msg);
	void registerChannel(int channel, UdpChannel udpChannel);
}
