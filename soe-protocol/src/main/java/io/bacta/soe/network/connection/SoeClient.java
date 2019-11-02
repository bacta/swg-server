package io.bacta.soe.network.connection;

import akka.actor.AbstractActor;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by kyle on 7/9/2017.
 */
@Slf4j
class SoeClient extends AbstractActor {

//    private InetSocketAddress remoteAddress;
//
//    private ActorRef outgoingMessageHandler;
//
//    private ConnectionState state;
//
//    private final GameNetworkMessageHandler messageHandler;
//
//    private final Set<ConnectionRole> roles;
//
//    private final SoeSessionContext sessionContext;
//
//
//    @Inject
//    public SoeClient(final GameNetworkMessageHandler messageHandler) {
//        log.debug("Creating new SoeClient - constructor: {}", getSelf());
//        this.messageHandler = messageHandler;
//        this.state = ConnectionState.ONLINE;
//        this.roles = new HashSet<>();
//        sessionContext = new AkkaSoeSessionContext(getSelf(), roles);
//    }
//
//    @Override
//    public void preStart() {
//        log.debug("SoeClient::preStart {}", getSelf());
//    }
//
//    private void configureConnection(ConfigureConnection updateGameRouterRef) {
//        log.debug("Configuring new SoeClient {}", getSelf());
//        outgoingMessageHandler = getContext().actorFor(getSender().path());
//        remoteAddress = updateGameRouterRef.getRemoteAddress();
//        sessionContext.setRemoteAddress(remoteAddress);
//    }
//
    @Override
    public Receive createReceive() {
        return receiveBuilder()
//                .match(ConfigureConnection.class, this::configureConnection)
//                .match(GameNetworkMessage.class, this::receiveGameNetworkMessage)
//                .match(SwgResponseMessage.class, this::sendGameNetworkMessage)
//                .match(SwgTerminateMessage.class, this::sendTerminate)
                .build();
    }
//
//    private void sendTerminate(SwgTerminateMessage terminateMessage) {
//        state = ConnectionState.DISCONNECTED;
//        outgoingMessageHandler.forward(terminateMessage, getContext());
//    }
//
//    private void receiveGameNetworkMessage(GameNetworkMessage message) {
//        SoeRequestContext requestContext = new SoeRequestContext(sessionContext);
//        messageHandler.handle(requestContext, message);
//    }
//
//    private void sendGameNetworkMessage(SwgResponseMessage message) {
//        log.debug("SoeClient:sendGameNetworkMessage {}", getSelf());
//        outgoingMessageHandler.forward(message, getContext());
//    }
}
