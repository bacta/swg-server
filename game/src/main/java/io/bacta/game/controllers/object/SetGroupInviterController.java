package io.bacta.game.controllers.object;

import io.bacta.game.GameControllerMessage;
import io.bacta.game.GameControllerMessageType;
import io.bacta.game.context.GameRequestContext;
import io.bacta.game.message.object.MessageQueueSetGroupInviter;
import io.bacta.game.object.ServerObject;
import io.bacta.game.object.tangible.creature.CreatureObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@GameControllerMessage(GameControllerMessageType.SET_GROUP_INVITER)
public final class SetGroupInviterController implements MessageQueueController<MessageQueueSetGroupInviter> {
    @Override
    public void handleIncoming(GameRequestContext context, ServerObject actor, int flags, float value, MessageQueueSetGroupInviter data) {
        final CreatureObject creatureObject = actor.asCreatureObject();

        if (creatureObject != null) {
            creatureObject.setGroupInviter(data.getInviterNetworkId(), data.getInviterName(), data.getInviterShipNetworkId());
        }
    }
}
