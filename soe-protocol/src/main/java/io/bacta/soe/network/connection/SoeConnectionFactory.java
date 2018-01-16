package io.bacta.soe.network.connection;

import java.net.InetSocketAddress;

public interface SoeConnectionFactory {
    SoeConnection newInstance(InetSocketAddress sender);
    SoeConnection newOutgoingInstance(final InetSocketAddress sender);
}
