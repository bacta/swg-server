package com.ocdsoft.bacta.soe.network.connection;

import com.codahale.metrics.Gauge;
import com.codahale.metrics.MetricRegistry;
import com.ocdsoft.bacta.soe.config.SoeNetworkConfiguration;
import com.ocdsoft.bacta.soe.network.message.TerminateReason;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.actuate.metrics.GaugeService;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by kyle on 5/28/2016.
 * This class houses and manages all the soe objects
 */
@Slf4j
public final class SoeConnectionCache {

    private final SoeNetworkConfiguration networkConfiguration;
    private final Map<InetSocketAddress, SoeUdpConnection> connectionMap;
    private final Map<Integer, Queue<SoeUdpConnection>> connectedAccountCache;
    private final Gauge<Integer> sizeGauge;

    @Inject
    public SoeConnectionCache(final SoeNetworkConfiguration networkConfiguration,
                              final MetricRegistry metricRegistry) {

        this.connectionMap = new ConcurrentHashMap<>();
        this.connectedAccountCache = new ConcurrentHashMap<>();
        this.networkConfiguration = networkConfiguration;
        this.sizeGauge = connectionMap::size;
        metricRegistry.register(networkConfiguration.getMetricsPrefix(), sizeGauge);
    }

    /**
     * Returns the number of connections currently recognized
     * @return number of connections
     */
    public int getConnectionCount() {
        return connectionMap.size();
    }

    /**
     * Add an incoming soe to the cache
     * @param remoteAddress address soe is coming from
     * @param connection new soe object
     */
    public void put(final InetSocketAddress remoteAddress, final SoeUdpConnection connection) {
        connectionMap.put(remoteAddress, connection);
        LOGGER.debug("New soe from {}.  Total clients: {}",
                remoteAddress,
                getConnectionCount());
    }

    /**
     * Get soe associated with this {@link InetSocketAddress}
     * @param sender
     * @return soe
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
     * Remove soe from cache based on {@link InetSocketAddress} key
     * @param inetSocketAddress
     * @return soe removed
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
     * Add new soe.  This method will disconnect any non-authenticated soe or a soe without associated BactaId
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
                LOGGER.trace("Account {} exceed the number of allowed connections ({}), terminating oldest soe {}",
                        connection.getAccountUsername(),
                        networkConfiguration.getConnectionsPerAccount(),
                        connection.getId());
            }
        } else {
            connectionQueue = new ArrayBlockingQueue<>(networkConfiguration.getConnectionsPerAccount());
            connectedAccountCache.put(connection.getBactaId(), connectionQueue);
            LOGGER.trace("Account {} is making first soe {}", connection.getAccountUsername(), connection.getId());
        }

        connectionQueue.add(connection);
    }
}
