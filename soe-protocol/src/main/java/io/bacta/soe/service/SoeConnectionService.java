package io.bacta.soe.service;

import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.soe.network.connection.SoeConnectionCache;
import io.bacta.soe.network.connection.SoeConnectionFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;

@Service
public class SoeConnectionService {

    private final SoeConnectionCache connectionCache;
    private final SoeConnectionFactory connectionProvider;

    @Inject
    public SoeConnectionService(final SoeConnectionCache connectionCache, final SoeConnectionFactory connectionProvider) {
        this.connectionCache = connectionCache;
        this.connectionProvider = connectionProvider;
    }

    public <T extends SoeConnection> WeakReference<T> getConnection(final InetSocketAddress address) {
        return getConnection((Class<T>) SoeConnection.class, address);
    }

    public <T extends SoeConnection> WeakReference<T> getConnection(final Class<T> connectionClass, final InetSocketAddress address) {
        T newConnection = (T) connectionProvider.newInstance(connectionClass, address);
        connectionCache.put(address, newConnection);

        return new WeakReference<T>(newConnection);
    }
}
