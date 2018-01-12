package io.bacta.soe.network.connection;

/**
 * Created by kyle on 7/9/2017.
 */
public class ClientConnection extends SoeConnection {
    public ClientConnection(SoeUdpConnection soeUdpConnection) {
        super(soeUdpConnection);
    }
}
