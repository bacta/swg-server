package io.bacta.soe.network.relay;

import akka.actor.ActorRef;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.context.AkkaSoeSessionContext;
import io.bacta.soe.event.DisconnectEvent;
import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.connection.SoeUdpConnectionCache;
import io.bacta.soe.network.handler.GameNetworkMessageHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * Forwards GameNetworkMessages from the connection server to the scene servers
 */
@Slf4j
public class AkkaGameNetworkMessageRelay implements GameNetworkMessageRelay {

    private final GameNetworkMessageHandler gameNetworkMessageHandler;
    private ActorRef transceiverRef;

    private final Cache<Integer, AkkaSoeSessionContext> sessionCache;
    private final SoeUdpConnectionCache connectionCache;

    public AkkaGameNetworkMessageRelay(final SoeUdpConnectionCache connectionCache, final GameNetworkMessageHandler gameNetworkMessageHandler) {
        this.connectionCache = connectionCache;
        this.gameNetworkMessageHandler = gameNetworkMessageHandler;

        this.sessionCache = CacheBuilder.newBuilder()
                .expireAfterAccess(20, TimeUnit.MINUTES)
                .removalListener((RemovalListener<Integer, AkkaSoeSessionContext>) removalNotification -> {
                    DisconnectEvent disconnectEvent = new DisconnectEvent(removalNotification.getValue());
                    ActorRef handler = removalNotification.getValue().getConnectionServerRef();
                    if(handler != null) {
                        handler.tell(disconnectEvent, ActorRef.noSender());
                    }
                    LOGGER.debug("Connection Ended: Connection: {}  Reason: Cache Expiration", removalNotification.getCause());
                })
                .initialCapacity(200)
                .maximumSize(3000)
                .build();
    }

    @Override
    public void receiveMessage(SoeUdpConnection connection, GameNetworkMessage gameNetworkMessage) {

        LOGGER.debug("Received Request: {} from {}", gameNetworkMessage.getClass().getSimpleName(), connection.getRemoteAddress());

        try {
            AkkaSoeSessionContext context = sessionCache.get(connection.getId(), () -> createNewSession(connection));
            assert context != null;
            ActorRef dispatcherRef = context.getSceneServerRef();
            if(dispatcherRef != null) {
                // Dispatch to Scene
                LOGGER.debug("Dispatching message {} to: {}", dispatcherRef, gameNetworkMessage.getClass().getSimpleName());
                SwgRequestMessage message = new SwgRequestMessage(context, gameNetworkMessage);
                dispatcherRef.tell(message, transceiverRef);
            } else {
                gameNetworkMessageHandler.handle(context, gameNetworkMessage);
            }

        } catch(Exception e) {
            LOGGER.error("Unhandled Exception in handleRequest", e);
        }
    }

    @Override
    public void sendMessage(int connectionId, GameNetworkMessage message) {
        connectionCache.sendMessage(connectionId, message);
    }

    private AkkaSoeSessionContext createNewSession(SoeUdpConnection connection) {
        return new AkkaSoeSessionContext(connection.getId(), transceiverRef);
    }

    public void setTransceiverRef(ActorRef transceiverRef) {
        this.transceiverRef = transceiverRef;
    }
}
