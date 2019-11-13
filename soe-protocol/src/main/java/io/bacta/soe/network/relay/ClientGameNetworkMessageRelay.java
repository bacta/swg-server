package io.bacta.soe.network.relay;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.context.ClientSoeSessionContext;
import io.bacta.soe.context.SoeSessionContext;
import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.connection.SoeUdpConnectionCache;
import io.bacta.soe.network.handler.GameNetworkMessageHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ClientGameNetworkMessageRelay implements GameNetworkMessageRelay {

    private final GameNetworkMessageHandler gameNetworkMessageHandler;
    private final SoeUdpConnectionCache connectionCache;
    private final Cache<Integer, SoeSessionContext> sessionCache;

    public ClientGameNetworkMessageRelay(SoeUdpConnectionCache connectionCache, GameNetworkMessageHandler gameNetworkMessageHandler) {
        this.connectionCache = connectionCache;
        this.gameNetworkMessageHandler = gameNetworkMessageHandler;
        this.sessionCache = CacheBuilder.newBuilder()
                .expireAfterAccess(20, TimeUnit.MINUTES)
                .removalListener((RemovalListener<Integer, SoeSessionContext>) removalNotification -> {
                    LOGGER.debug("Connection Ended: Connection: {}  Reason: Cache Expiration", removalNotification.getCause());
                })
                .initialCapacity(1)
                .maximumSize(2)
                .build();
    }

    @Override
    public void receiveMessage(SoeUdpConnection connection, GameNetworkMessage gameNetworkMessage) {
        try {
            SoeSessionContext context = sessionCache.get(connection.getId(), () -> createNewSession(connection));
            assert context != null;

            gameNetworkMessageHandler.handle(context, gameNetworkMessage);

        } catch(ExecutionException e) {
            LOGGER.error("Unhandled Exception in handleRequest", e);
        }
    }

    @Override
    public void sendMessage(int connectionId, GameNetworkMessage message) {
        connectionCache.sendMessage(connectionId, message);
    }

    private SoeSessionContext createNewSession(SoeUdpConnection connection) {
        return new ClientSoeSessionContext(connection.getId());
    }
}
