package bacta.io.network.udp;

import bacta.io.network.ConnectionState;

import java.net.InetSocketAddress;

public interface UdpConnection {
	InetSocketAddress getRemoteAddress();
    ConnectionState getState();
    void setState(ConnectionState state);
}
