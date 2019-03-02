package io.bacta.soe.network.connection;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import io.bacta.engine.network.connection.ConnectionState;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.context.SoeRequestContext;
import io.bacta.soe.network.forwarder.SwgResponseMessage;
import io.bacta.soe.network.handler.GameNetworkMessageHandler;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.InetSocketAddress;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by kyle on 7/9/2017.
 */
@Component
@Scope("prototype")
class SoeClient extends AbstractActor {

    private InetSocketAddress remoteAddress;

    private ActorRef outgoingMessageHandler;

    private ConnectionState state;

    private final GameNetworkMessageHandler messageHandler;

    private final Set<ConnectionRole> roles;

    @Inject
    public SoeClient(final GameNetworkMessageHandler messageHandler) {
        this.messageHandler = messageHandler;
        this.state = ConnectionState.ONLINE;
        this.roles = new HashSet<>();
    }

    private void configureConnection(ConfigureConnection updateGameRouterRef) {
        outgoingMessageHandler = getSender();
        remoteAddress = updateGameRouterRef.getRemoteAddress();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ConfigureConnection.class, this::configureConnection)
                .match(GameNetworkMessage.class, this::receiveGameNetworkMessage)
                .match(SwgResponseMessage.class, this::sendGameNetworkMessage)
                .build();
    }

    private void receiveGameNetworkMessage(GameNetworkMessage message) {
        SoeRequestContext requestContext = new SoeRequestContext(getSelf(), roles, remoteAddress);
        messageHandler.handle(requestContext, message);
    }

    private void sendGameNetworkMessage(SwgResponseMessage message) {
        outgoingMessageHandler.tell(message, getSelf());
    }
}
