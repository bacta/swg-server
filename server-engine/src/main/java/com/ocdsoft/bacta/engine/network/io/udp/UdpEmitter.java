package com.ocdsoft.bacta.engine.network.io.udp;

/**
 * 
 * @author Kyle Burkhardt
 * @since 1.0
 *
 * @param <Data> Data type the implementation writes to the socket is.
 */
public interface UdpEmitter<Connection, Data> {
	void sendMessage(Connection connection, Data msg);
}
