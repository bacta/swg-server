package com.ocdsoft.bacta.engine.network.io.udp;

/**
 * 
 * @author Kyle Burkhardt
 * @since 1.0
 *
 * @param <Data> The datatype received from the socket
 */
public interface UdpReceiver<Client, Data> {

	void receiveMessage(Client client, Data msg);

}
