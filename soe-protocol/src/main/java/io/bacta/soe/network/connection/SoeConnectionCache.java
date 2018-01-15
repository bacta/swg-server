package io.bacta.soe.network.connection;

import io.bacta.shared.GameNetworkMessage;

import java.net.InetSocketAddress;
import java.util.Set;

public interface SoeConnectionCache {
    int getConnectionCount();
    void put(InetSocketAddress remoteAddress, SoeConnection connection);
    SoeConnection get(InetSocketAddress sender);
    Set<InetSocketAddress> keySet();
    SoeConnection remove(InetSocketAddress inetSocketAddress);
    void broadcast(GameNetworkMessage message);
}
