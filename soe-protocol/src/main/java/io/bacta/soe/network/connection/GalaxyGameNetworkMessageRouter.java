package io.bacta.soe.network.connection;

import akka.actor.*;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.japi.pf.DeciderBuilder;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.soe.event.DisconnectEvent;
import io.bacta.soe.network.forwarder.SwgRequestMessage;
import io.bacta.soe.network.forwarder.SwgResponseMessage;
import io.bacta.soe.network.handler.GameNetworkMessageHandler;
import io.bacta.soe.network.message.SwgTerminateMessage;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import scala.concurrent.duration.Duration;

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
                    log.debug("Connection Ended: Connection: {}  Reason: Cache Expiration", removalNotification.getCause());
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
                .match(SwgTerminateMessage.class, this::handleTerminate)
                //.match(ZoneConnectionAdded.class, this::zoneAddConnection)
                //.match(ZoneConnectionRemoved.class, this::zoneRemoveConnection)
                .build();
    }

    /**
     * Forward message to connection actor
     * @param request
     */
    private void handleRequest(final SwgRequestMessage request) throws ExecutionException {
        log.debug("Received Request: {} from {}", request.getGameNetworkMessage().getClass().getSimpleName(), request.getRemoteAddress());

        try {
            ActorRef client = clientCache.get(request.getRemoteAddress(), () -> {
                SoeUdpConnection connection = connectionCache.getIfPresent(request.getRemoteAddress());
                log.debug("Creating new SOEClient: {}", String.valueOf(connection.getId()));
                ActorRef newConnection = getContext().actorOf(ext.props(SoeClient.class), String.valueOf(connection.getId()));
                log.debug("Using New client: {} for {}", newConnection, request.getGameNetworkMessage().getClass().getSimpleName());
                newConnection.tell(new ConfigureConnection(request.getRemoteAddress()), getSelf());
                return newConnection;
            });
            log.debug("Using client: {} for {}", client, request.getGameNetworkMessage().getClass().getSimpleName());
            client.tell(request.getGameNetworkMessage(), getSelf());
        } catch(Exception e) {
            log.error(e, "Unhandled Exception in handleRequest");
        }
    }

    private void handleResponse(final SwgResponseMessage response) {
        log.debug("Received Response: {} from {}", response.getMessage(), getSender());
        try {
            SoeUdpConnection connection = connectionCache.getIfPresent(response.getRemoteSender());
            if (connection != null) {
                connection.sendMessage(response.getMessage());
            }
        } catch(Exception e) {
            log.error(e, "Unhandled Exception in handleResponse");
        }
    }

    private void handleTerminate(SwgTerminateMessage terminateMessage) {
        log.debug("Received terminate from: {}", getSender());
        try {
            SoeUdpConnection connection = connectionCache.getIfPresent(terminateMessage.getRemoteAddress());
            connection.terminate(terminateMessage.getReason(), terminateMessage.isSilent());
            connectionCache.invalidate(terminateMessage.getRemoteAddress());
        } catch(Exception e) {
            log.error(e, "Unhandled Exception in handleTerminate");
        }
    }

    private static SupervisorStrategy strategy =
            new OneForOneStrategy(0, Duration.create(100, TimeUnit.MILLISECONDS), DeciderBuilder.
//                    match(ArithmeticException.class, e -> resume()).
//                    match(NullPointerException.class, e -> restart()).
//                    match(IllegalArgumentException.class, e -> stop()).
                    matchAny(o -> SupervisorStrategy.resume()).build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return strategy;
    }
}
