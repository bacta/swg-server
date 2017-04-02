package com.ocdsoft.bacta.soe.protocol.io.udp;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.ocdsoft.bacta.soe.protocol.connection.ConnectionRole;
import com.ocdsoft.bacta.soe.protocol.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.protocol.message.TerminateReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
@Singleton
public final class AccountCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountCache.class);

    private final NetworkConfiguration networkConfiguration;
    private final Map<InetSocketAddress, SoeUdpConnection> connectionMap;
    private final Map<Integer, Queue<SoeUdpConnection>> connectedAccountCache;

    @Inject
    public AccountCache(final NetworkConfiguration networkConfiguration) {
        this.connectionMap = new ConcurrentHashMap<>();
        this.connectedAccountCache = new ConcurrentHashMap<>();
        this.networkConfiguration = networkConfiguration;
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
        LOGGER.trace("Adding connection from {}", remoteAddress);
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
                    LOGGER.trace("No more connections for account {}", connection.getAccountUsername());
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
            LOGGER.error("Connection {} is not associated with an account and authenticated is {}, terminating", connection.getId(), connection.hasRole(ConnectionRole.AUTHENTICATED));
            return;
        }

        Queue<SoeUdpConnection> connectionQueue = connectedAccountCache.get(connection.getBactaId());
        if (connectionQueue != null) {
            while (connectionQueue.size() >= networkConfiguration.getConnectionsPerAccount()) {
                SoeUdpConnection connectionToDisconnect = connectionQueue.poll();
                connectionToDisconnect.terminate(TerminateReason.NEWATTEMPT);
                LOGGER.trace("Account {} exceed the number of allowed connections ({}), terminating oldest connection {}",
                        connection.getAccountUsername(),
                        networkConfiguration.getConnectionsPerAccount(),
                        connection.getId());
            }
        } else {
            connectionQueue = new ArrayBlockingQueue<>(networkConfiguration.getConnectionsPerAccount());
            connectedAccountCache.put(connection.getBactaId(), connectionQueue);
            LOGGER.trace("Account {} is making first connection {}", connection.getAccountUsername(), connection.getId());
        }

        connectionQueue.add(connection);
    }
}
