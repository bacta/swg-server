package io.bacta.soe.network.connection;

import java.net.InetSocketAddress;

public interface ConnectionMap {
    SoeConnection getOrCreate(final InetSocketAddress address);
}
