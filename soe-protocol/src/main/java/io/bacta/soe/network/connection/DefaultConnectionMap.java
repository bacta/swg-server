package io.bacta.soe.network.connection;

import io.bacta.shared.GameNetworkMessage;

import java.net.InetSocketAddress;
import java.util.function.Consumer;
import java.util.function.Function;


/**
 *  Holder class for the connection retrieval method
 */
public class DefaultConnectionMap implements ConnectionMap {

    private Function<InetSocketAddress, SoeConnection> getConnectionMethod;
    private Consumer<GameNetworkMessage> broadcastMethod;

    @Override
    public void setGetConnectionMethod(Function<InetSocketAddress, SoeConnection> getConnectionMethod) {
        this.getConnectionMethod = getConnectionMethod;
    }

    @Override
    public SoeConnection getOrCreate(InetSocketAddress address) {
        return this.getConnectionMethod.apply(address);
    }

    @Override
    public void broadcast(GameNetworkMessage message) {
        this.broadcastMethod.accept(message);
    }

    @Override
    public void setBroadcastMethod(Consumer<GameNetworkMessage> broadcastMethod) {
        this.broadcastMethod = broadcastMethod;
    }
}
