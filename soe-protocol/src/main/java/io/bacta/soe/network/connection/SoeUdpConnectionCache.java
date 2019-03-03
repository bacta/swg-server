package io.bacta.soe.network.connection;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import io.bacta.engine.network.connection.ConnectionState;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.config.SoeNetworkConfiguration;
import io.bacta.soe.event.UdpConnectEvent;
import io.bacta.soe.event.UdpDisconnectEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public final class SoeUdpConnectionCache {

    private final LoadingCache<InetSocketAddress, SoeUdpConnection> connectionCache;

    @Inject
    public SoeUdpConnectionCache(final ApplicationEventPublisher publisher,
                                 final SoeNetworkConfiguration networkConfiguration,
                                 final SoeUdpConnectionFactory udpConnectionFactory) {

        this.connectionCache = CacheBuilder.newBuilder()
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .removalListener((RemovalListener<InetSocketAddress, SoeUdpConnection>) removalNotification -> {
                    publisher.publishEvent(new UdpDisconnectEvent(removalNotification.getKey()));
                    if (networkConfiguration.isReportUdpDisconnects()) {
                        SoeUdpConnection connection = removalNotification.getValue();
                        LOGGER.info("Client disconnected: {}  Connection: {}  Reason: {}", connection.getRemoteAddress(), connection.getId(), connection.getTerminateReason().getReason());
                        LOGGER.info("Clients still connected: {}", size());
                    }
                })
                .initialCapacity(200)
                .maximumSize(3000)
                .build(new CacheLoader<InetSocketAddress, SoeUdpConnection>() {
                    @Override
                    public SoeUdpConnection load(InetSocketAddress sender) throws Exception {
                        SoeUdpConnection connection = udpConnectionFactory.newInstance(sender);
                        connection.setConnectionState(ConnectionState.ONLINE);
                        connectionCache.invalidate(sender);
                        connectionCache.put(sender, connection);
                        publisher.publishEvent(new UdpConnectEvent(connection));
                        return connection;
                    }
                });
    }

    public long size() {
        return connectionCache.size();
    }

    public SoeUdpConnection getIfPresent(InetSocketAddress sender) {
        return connectionCache.getIfPresent(sender);
    }

    public SoeUdpConnection getNewConnection(InetSocketAddress sender) {
        try {
            SoeUdpConnection connection = getIfPresent(sender);
            if(connection != null) {
                invalidate(sender);
            }
            return connectionCache.get(sender);
        } catch (ExecutionException e) {
            throw new RuntimeException("Unable to get or load connection", e);
        }
    }

    public Map<InetSocketAddress, SoeUdpConnection> asMap() {
        return connectionCache.asMap();
    }

    public void invalidate(InetSocketAddress inetSocketAddress) {
        connectionCache.invalidate(inetSocketAddress);
    }

    void broadcast(GameNetworkMessage message) {
        connectionCache.asMap().forEach((inetSocketAddress, soeUdpConnection) -> {
            soeUdpConnection.sendMessage(message);
        });
    }
}
