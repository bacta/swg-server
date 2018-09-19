package io.bacta.game.controllers.object;

import io.bacta.game.GameControllerMessage;
import io.bacta.game.GameControllerMessageType;
import io.bacta.game.context.GameRequestContext;
import io.bacta.game.message.object.MessageQueueNetworkId;
import io.bacta.game.object.ServerObject;
import io.bacta.game.object.tangible.creature.CreatureObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@GameControllerMessage(GameControllerMessageType.CLIENT_INTENDED_TARGET)
public final class ClientIntendedTargetController implements MessageQueueController<MessageQueueNetworkId> {
    @Override
    public void handleIncoming(GameRequestContext context, ServerObject actor, int flags, float value, MessageQueueNetworkId data) {
        final CreatureObject creo = actor.asCreatureObject();

        if (creo == null) {
            LOGGER.error("Received client intended target message for non-creature object {}.", actor.getNetworkId());
            return;
        }

        //TODO: Implement
        //creo.setIntendedTarget(data.getNetworkId());
        LOGGER.warn("setIntended target is not implemented on creature object yet.");
    }
}
