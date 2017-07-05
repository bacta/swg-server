package bacta.io.soe.service;

import bacta.io.soe.network.connection.SoeUdpConnection;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

/**
 * Created by kburkhardt on 2/5/15.
 */
public interface OutgoingConnectionService {
    SoeUdpConnection createOutgoingConnection(final InetSocketAddress inetSocketAddress, final Consumer<SoeUdpConnection> connectCallback);
}
