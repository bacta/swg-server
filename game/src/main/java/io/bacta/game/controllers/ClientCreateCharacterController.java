package io.bacta.game.controllers;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import io.bacta.game.galaxy.GalaxyTopics;
import io.bacta.game.message.ClientCreateCharacter;
import io.bacta.soe.context.SoeRequestContext;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
@MessageHandled(handles = ClientCreateCharacter.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class ClientCreateCharacterController implements GameNetworkMessageController<SoeRequestContext, ClientCreateCharacter> {
    private final ActorSystem actorSystem;

    @Inject
    public ClientCreateCharacterController(ActorSystem actorSystem) {
        this.actorSystem = actorSystem;
    }

    @Override
    public void handleIncoming(SoeRequestContext context, ClientCreateCharacter message) throws Exception {
        //this.creationService.createCharacter(context, message);
        final ActorRef client = context.getSessionContext().getSoeClient();
        final ActorRef mediator = DistributedPubSub.get(actorSystem).mediator();
        mediator.tell(new DistributedPubSubMediator.Publish(GalaxyTopics.PLAYER_CREATION, message), client);
    }
}

