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
@GameControllerMessage(GameControllerMessageType.CLIENT_LOOK_AT_TARGET)
public final class ClientLookAtTargetController implements MessageQueueController<MessageQueueNetworkId> {
    @Override
    public void handleIncoming(GameRequestContext context, ServerObject actor, int flags, float value, MessageQueueNetworkId data) {
        final CreatureObject creo = actor.asCreatureObject();

        if (creo == null) {
            LOGGER.error("Received client look at target message for non-creature object {}.", actor.getNetworkId());
            return;
        }

        creo.setLookAtTarget(data.getNetworkId());
    }
}
