package io.bacta.soe.network.connection;

/**
 * Created by kyle on 7/9/2017.
 */
public class GameServerConnection extends SoeConnection {
    public GameServerConnection(SoeUdpConnection soeUdpConnection) {
        super(soeUdpConnection);
    }
}
