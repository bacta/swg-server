package io.bacta.soe.network.connection;

import akka.actor.ActorRef;
import io.bacta.engine.network.connection.ConnectionState;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.context.SoeRequestContext;
import io.bacta.soe.context.SoeSessionContext;
import io.bacta.soe.network.dispatch.GameNetworkMessageDispatcher;
import io.bacta.soe.network.forwarder.SwgRequestMessage;
import io.bacta.soe.network.forwarder.SwgResponseMessage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.net.InetSocketAddress;

/**
 * Created by kyle on 7/9/2017.
 */
@Component
@Scope("prototype")
class SoeClient {

    private InetSocketAddress remoteAddress;

    private int bactaId;
    private String bactaUsername;

    private long currentNetworkId;
    private String currentCharName;

    private ActorRef messageRouter;

    private ConnectionState state;

    private final GameNetworkMessageDispatcher dispatcher;

    private SoeSessionContext context;

    @Inject
    public SoeClient(final GameNetworkMessageDispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.state = ConnectionState.ONLINE;
    }

    private void configureConnection(ConfigureConnection updateGameRouterRef) {
        messageRouter = getSender();
        remoteAddress = updateGameRouterRef.getRemoteAddress();
        context = new SoeSessionContext(getSelf(), remoteAddress, this::handleResponse);
    }

    private void handleRequest(SwgRequestMessage request) {
        if(context == null) {
            throw new SoeContextNotReadyException();
        }
        SoeRequestContext requestContext = context.newRequest();
        dispatcher.dispatch(request.getZeroByte(), request.getOpcode(), requestContext, request.getBuffer());
    }

    private void handleResponse(GameNetworkMessage response) {
        messageRouter.tell(new SwgResponseMessage(response, remoteAddress), getSelf());
    }
}
