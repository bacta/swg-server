package io.bacta.soe.network.connection;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.PoisonPill;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.soe.event.DisconnectEvent;
import io.bacta.soe.network.forwarder.SwgRequestMessage;
import io.bacta.soe.network.forwarder.SwgResponseMessage;
import io.bacta.soe.network.handler.GameNetworkMessageHandler;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class GalaxyGameNetworkMessageRouter extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), GalaxyGameNetworkMessageRouter.class);
    private final SoeUdpConnectionCache connectionCache;
    private final Cache<InetSocketAddress, ActorRef> clientCache;
    private final SpringAkkaExtension ext;
    private final GameNetworkMessageHandler dispatcher;
    private final ApplicationEventPublisher publisher;

    @Inject
    public GalaxyGameNetworkMessageRouter(final ApplicationEventPublisher publisher,
                                          final SoeUdpConnectionCache connectionCache,
                                          final GameNetworkMessageHandler dispatcher,
                                          final SpringAkkaExtension ext) {
        this.publisher = publisher;
        this.connectionCache = connectionCache;
        this.dispatcher = dispatcher;
        this.ext = ext;
        this.clientCache = CacheBuilder.newBuilder()
                .expireAfterAccess(5, TimeUnit.MINUTES)
                .removalListener((RemovalListener<InetSocketAddress, ActorRef>) removalNotification -> {
                    publisher.publishEvent(new DisconnectEvent(removalNotification.getKey()));
                    removalNotification.getValue().tell(PoisonPill.getInstance(), ActorRef.noSender());
                    log.info("Connection Ended: {}  Connection: {}  Reason: {}");
                })
                .initialCapacity(200)
                .maximumSize(3000)
                .build();
    }

    @Override
    public void preStart() throws Exception {
        log.info("GNM Router starting");
        super.preStart();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SwgRequestMessage.class, this::handleRequest)
                .match(SwgResponseMessage.class, this::handleResponse)
                //.match(ZoneConnectionAdded.class, this::zoneAddConnection)
                //.match(ZoneConnectionRemoved.class, this::zoneRemoveConnection)
                .build();
    }

    /**
     * Forward message to connection actor
     * @param request
     */
    private void handleRequest(final SwgRequestMessage request) throws ExecutionException {
        if(log.isDebugEnabled()) {
            log.debug("Received Request: {} from {}", request.getGameNetworkMessage().getClass().getSimpleName(), request.getRemoteAddress());
        }
        ActorRef client = clientCache.get(request.getRemoteAddress(), () -> {
            ActorRef newConnection = getContext().actorOf(ext.props(SoeClient.class), request.getRemoteAddress().toString());
            clientCache.put(request.getRemoteAddress(), newConnection);
            newConnection.tell(new ConfigureConnection(request.getRemoteAddress()), getSelf());
            return newConnection;
        });
        client.tell(request.getGameNetworkMessage(), getSelf());
    }

    private void handleResponse(final SwgResponseMessage response) {
        log.debug("Received Response Opcode: {} from {} with content {}");
        SoeUdpConnection connection = connectionCache.getIfPresent(response.getRemoteSender());
        if(connection != null) {
            connection.sendMessage(response.getMessage());
        }
    }
}
