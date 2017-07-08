package io.bacta.soe.service;

import io.bacta.network.ConnectionState;
import io.bacta.soe.config.SoeNetworkConfiguration;
import io.bacta.soe.network.connection.SoeConnectionProvider;
import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.connection.SoeUdpConnectionCache;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Created by kyle on 7/7/2017.
 */

@Component
public class DefaultInternalMessageService implements InternalMessageService {

    private final SoeNetworkConfiguration networkConfiguration;
    private SoeUdpConnectionCache connectionCache;
    private SoeConnectionProvider connectionProvider;

    public DefaultInternalMessageService(final SoeNetworkConfiguration networkConfiguration) {
        this.networkConfiguration = networkConfiguration;
    }

    @Override
    public void setConnectionCache(final SoeUdpConnectionCache connectionCache) {
        this.connectionCache = connectionCache;
    }

    @Override
    public void setConnectionProvider(SoeConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public SoeUdpConnection getSessionServerConnection() {
        return null;
    }

    @Override
    public SoeUdpConnection getGalaxyServerConnection() {
        return null;
    }

    @Override
    public SoeUdpConnection getConnectionServerConnection() {
        return null;
    }

    @Override
    public SoeUdpConnection getLoginServerConnection() {
        SoeUdpConnection connection = connectionProvider.newInstance(new InetSocketAddress(InetAddress.getLoopbackAddress(), 44454));
        connection.setState(ConnectionState.NEW);
        connectionCache.put(connection.getRemoteAddress(), connection);
        connection.connect();
        //RequestReplyHelper

        return connection;
    }


}
