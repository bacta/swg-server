package io.bacta.soe.service;

import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.SoeConnectionConfiguration;
import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.soe.network.connection.SoeConnectionCache;
import io.bacta.soe.network.connection.SoeConnectionFactory;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class SoeConnectionService {

    private final SoeConnectionCache connectionCache;
    private final SoeConnectionFactory connectionProvider;
    private final SoeConnectionConfiguration soeConnectionConfiguration;

    private final Map<Class<? extends SoeConnection>, Set<WeakReference<? extends SoeConnection>>> connectionMap;

    @Inject
    public SoeConnectionService(final SoeConnectionCache connectionCache, final SoeConnectionFactory connectionProvider, final SoeConnectionConfiguration soeConnectionConfiguration) {
        this.connectionCache = connectionCache;
        this.connectionProvider = connectionProvider;
        this.soeConnectionConfiguration = soeConnectionConfiguration;

        this.connectionMap = new HashMap<>();
    }

    public <T extends SoeConnection> WeakReference<T> getConnection(final Class<T> connectionClass) {

        Set<WeakReference<? extends SoeConnection>> connectionReferences = connectionMap.get(connectionClass);
        if(connectionReferences == null || connectionReferences.isEmpty()) {
            //connectionReference = connect(connectionClass);
        }

        return null;
    }

    public void broadcastMessage(final Class<? extends SoeConnection> connectionClass, final GameNetworkMessage message) {

    }

    private WeakReference<? extends SoeConnection> connect(final Class<? extends SoeConnection> connectionClass) {
        InetSocketAddress[] hosts = soeConnectionConfiguration.getAddresses(connectionClass);

        return null;
    }
}
