package com.ocdsoft.bacta.soe.protocol.network.io.udp;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.soe.protocol.network.connection.ConnectionRole;
import com.ocdsoft.bacta.soe.protocol.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.protocol.network.message.TerminateReason;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kyle on 5/28/2016.
 * This class houses and manages all the connection objects
 */
@Slf4j
@Singleton
public final class ConnectionCache {

    private final SoeNetworkConfiguration networkConfiguration;
    private final Map<InetSocketAddress, SoeUdpConnection> connectionMap;
    private final Map<Integer, Queue<SoeUdpConnection>> connectedAccountCache;

    @Inject
    public ConnectionCache(final SoeNetworkConfiguration networkConfiguration, final MetricRegistry metrics) {
        this.connectionMap = new ConcurrentHashMap<>();
        this.connectedAccountCache = new ConcurrentHashMap<>();
        this.networkConfiguration = networkConfiguration;

        metrics.register(MetricRegistry.name(ConnectionCache.class, "active-connections"),
                (Gauge<Integer>) this::getConnectionCount);
    }

    /**
     * Returns the number of connections currently recognized
     * @return number of connections
     */
    public int getConnectionCount() {
        return connectionMap.size();
    }

    /**
     * Add an incoming connection to the cache
     * @param remoteAddress address connection is coming from
     * @param connection new connection object
     */
    public void put(final InetSocketAddress remoteAddress, final SoeUdpConnection connection) {
        connectionMap.put(remoteAddress, connection);
        log.debug("New connection from {}.  Total clients: {}",
                remoteAddress,
                getConnectionCount());
    }

    /**
     * Get connection associated with this {@link InetSocketAddress}
     * @param sender
     * @return connection
     */
    public SoeUdpConnection get(final InetSocketAddress sender) {
        return connectionMap.get(sender);
    }

    /**
     * Retrieve all {@link InetSocketAddress} objects in the cache
     * @return all {@link InetSocketAddress} objects
     */
    public Set<InetSocketAddress> keySet() {
        return connectionMap.keySet();
    }

    /**
     * Remove connection from cache based on {@link InetSocketAddress} key
     * @param inetSocketAddress
     * @return connection removed
     */
    public SoeUdpConnection remove(final InetSocketAddress inetSocketAddress) {
        final SoeUdpConnection connection = connectionMap.remove(inetSocketAddress);

        if(connection != null) {
            Queue<SoeUdpConnection> connectionQueue = connectedAccountCache.get(connection.getBactaId());
            if (connectionQueue != null) {
                connectionQueue.remove(connection);
                if (connectionQueue.isEmpty()) {
                    connectedAccountCache.remove(connection.getBactaId());
                    log.trace("No more connections for account {}", connection.getAccountUsername());
                }
            }
        }

        return connection;
    }

    /**
     * Add new connection.  This method will disconnect any non-authenticated connection or a connection without associated BactaId
     * @param connection
     */
    public void addAccountConnection(final SoeUdpConnection connection) {

        // If someone tries to add an account before it has been authenticated, disconnect it
        if(!(connection.getBactaId() >= 0) || !connection.hasRole(ConnectionRole.AUTHENTICATED)) {
            connection.terminate(TerminateReason.NEWATTEMPT);
            log.error("Connection {} is not associated with an account and authenticated is {}, terminating", connection.getId(), connection.hasRole(ConnectionRole.AUTHENTICATED));
            return;
        }

        Queue<SoeUdpConnection> connectionQueue = connectedAccountCache.get(connection.getBactaId());
        if (connectionQueue != null) {
            while (connectionQueue.size() >= networkConfiguration.getConnectionsPerAccount()) {
                SoeUdpConnection connectionToDisconnect = connectionQueue.poll();
                connectionToDisconnect.terminate(TerminateReason.NEWATTEMPT);
                log.trace("Account {} exceed the number of allowed connections ({}), terminating oldest connection {}",
                        connection.getAccountUsername(),
                        networkConfiguration.getConnectionsPerAccount(),
                        connection.getId());
            }
        } else {
            connectionQueue = new ArrayBlockingQueue<>(networkConfiguration.getConnectionsPerAccount());
            connectedAccountCache.put(connection.getBactaId(), connectionQueue);
            log.trace("Account {} is making first connection {}", connection.getAccountUsername(), connection.getId());
        }

        connectionQueue.add(connection);
    }
}
