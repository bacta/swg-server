package io.bacta.soe.network.connection;

import java.net.InetSocketAddress;
import java.util.function.Function;

public interface ConnectionMap {
    void setGetConnectionMethod(Function<InetSocketAddress, SoeConnection> getConnectionMethod);
    SoeConnection getOrCreate(final InetSocketAddress address);
}
