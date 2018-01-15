package io.bacta.soe.network.connection;

import io.bacta.shared.GameNetworkMessage;

import java.net.InetSocketAddress;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ConnectionMap {
    void setGetConnectionMethod(Function<InetSocketAddress, SoeConnection> getConnectionMethod);
    void setBroadcastMethod(Consumer<GameNetworkMessage> broadcastMethod);

    SoeConnection getOrCreate(final InetSocketAddress address);
    void broadcast(GameNetworkMessage message);
}
