package io.bacta.soe.network.connection;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.config.SoeNetworkConfiguration;
import io.bacta.soe.event.UdpConnectEvent;
import io.bacta.soe.event.UdpDisconnectEvent;
import io.bacta.soe.network.message.ConfirmMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public final class SoeUdpConnectionCache {

    private final ApplicationEventPublisher publisher;
    private final Cache<InetSocketAddress, SoeUdpConnection> connectionCache;
    private final DefaultSoeUdpConnectionFactory udpConnectionFactory;
    private final TIntObjectMap<SoeUdpConnection> connectionIdMap;
    private final MBeanServer mBeanServer;

    @Inject
    public SoeUdpConnectionCache(final ApplicationEventPublisher publisher,
                                 final SoeNetworkConfiguration networkConfiguration,
                                 final DefaultSoeUdpConnectionFactory udpConnectionFactory) {

        this.publisher = publisher;
        this.connectionIdMap = new TIntObjectHashMap<>();
        this.udpConnectionFactory = udpConnectionFactory;
        this.mBeanServer = ManagementFactory.getPlatformMBeanServer();
        this.connectionCache = CacheBuilder.newBuilder()
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .removalListener((RemovalListener<InetSocketAddress, SoeUdpConnection>) removalNotification -> {
                    publisher.publishEvent(new UdpDisconnectEvent(removalNotification.getKey()));
                    SoeUdpConnection connection = removalNotification.getValue();
                    connectionIdMap.remove(connection.getId());
                    if (networkConfiguration.isReportUdpDisconnects()) {
                        LOGGER.info("Client disconnected: {}  Connection: {}  Reason: {}", connection.getRemoteAddress(), connection.getId(), connection.getTerminateReason().getReason());
                        LOGGER.info("Clients still connected: {}", size());
                    }
                })
                .initialCapacity(200)
                .maximumSize(3000)
                .build();
    }

    public long size() {
        return connectionCache.size();
    }

    public SoeUdpConnection getIfPresent(InetSocketAddress sender) {
        return connectionCache.getIfPresent(sender);
    }

    public SoeUdpConnection getNewConnection(InetSocketAddress sender) {
        return udpConnectionFactory.newInstance(sender);
    }

    public void confirm(SoeUdpConnection connection) {

        connectionCache.put(connection.getRemoteAddress(), connection);
        connectionIdMap.put(connection.getId(), connection);
        publisher.publishEvent(new UdpConnectEvent(connection));

        ConfirmMessage response = new ConfirmMessage(connection);
        connection.sendMessage(response);

        try {
            mBeanServer.registerMBean(connection, connection.getObjectName());
        } catch (InstanceAlreadyExistsException | NotCompliantMBeanException | MBeanRegistrationException e) {
            LOGGER.error("Unable to register MBean", e);
        }
    }

    public void invalidate(InetSocketAddress inetSocketAddress) {
        connectionCache.invalidate(inetSocketAddress);
    }

    public void broadcast(GameNetworkMessage message) {
        connectionCache.asMap().forEach((inetSocketAddress, soeUdpConnection) -> {
            soeUdpConnection.sendMessage(message);
        });
    }

    public void sendMessage(final int connectionId, final GameNetworkMessage message) {
        SoeUdpConnection connection = connectionIdMap.get(connectionId);
        if(connection != null) {
            connection.sendMessage(message);
        }
    }

    public Collection<SoeUdpConnection> getAsCollection() {
        return connectionCache.asMap().values();
    }
}
