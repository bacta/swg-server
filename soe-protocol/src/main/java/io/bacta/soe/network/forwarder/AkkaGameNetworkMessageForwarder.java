package io.bacta.soe.network.forwarder;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import io.bacta.actor.ActorConstants;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.SoeUdpConnection;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
public class AkkaGameNetworkMessageForwarder implements GameNetworkMessageForwarder {

    private final ActorRef galaxyUdpMessageDispatcher;

    @Inject
    public AkkaGameNetworkMessageForwarder(final ActorSystem actorSystem, final SpringAkkaExtension ext) {
        this.galaxyUdpMessageDispatcher = actorSystem.actorFor(ActorConstants.GAME_NETWORK_ROUTER);
    }

    @Override
    public void forward(SoeUdpConnection connection, GameNetworkMessage gameNetworkMessage) {
        SwgRequestMessage message = new SwgRequestMessage(connection.getRemoteAddress(), gameNetworkMessage);
        galaxyUdpMessageDispatcher.tell(message, ActorRef.noSender());
    }
}
