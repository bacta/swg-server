package io.bacta.soe.network.connection;

import java.net.InetSocketAddress;
import java.util.function.Function;


/**
 *  Holder class for the connection retrieval method
 */
public class DefaultConnectionMap implements ConnectionMap {

    private Function<InetSocketAddress, SoeConnection> getConnectionMethod;

    @Override
    public void setGetConnectionMethod(Function<InetSocketAddress, SoeConnection> getConnectionMethod) {
        this.getConnectionMethod = getConnectionMethod;
    }

    @Override
    public SoeConnection getOrCreate(InetSocketAddress address) {
        return getConnectionMethod.apply(address);
    }
}
