package io.bacta.soe.network.forwarder;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import io.bacta.actor.ActorConstants;
import io.bacta.engine.SpringAkkaExtension;
import io.bacta.soe.network.connection.SoeUdpConnection;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.nio.ByteBuffer;

@Component
public class AkkaGameNetworkMessageForwarder implements GameNetworkMessageForwarder {

    private final ActorRef localRouter;

    @Inject
    public AkkaGameNetworkMessageForwarder(final ActorSystem actorSystem, final SpringAkkaExtension ext) {
        this.localRouter = actorSystem.actorFor(ActorConstants.GAME_NETWORK_ROUTER);
    }

    @Override
    public void forward(byte zeroByte, int opcode, SoeUdpConnection connection, ByteBuffer buffer) {
        SwgRequestMessage message = new SwgRequestMessage(zeroByte, opcode, connection.getRemoteAddress(),  buffer);
        localRouter.tell(message, ActorRef.noSender());
    }
}
