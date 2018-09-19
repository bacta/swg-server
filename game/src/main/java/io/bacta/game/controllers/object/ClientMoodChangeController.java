package io.bacta.game.controllers.object;

import io.bacta.game.GameControllerMessage;
import io.bacta.game.GameControllerMessageType;
import io.bacta.game.MessageQueueData;
import io.bacta.game.context.GameRequestContext;
import io.bacta.game.object.ServerObject;
import io.bacta.game.object.tangible.creature.CreatureObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@GameControllerMessage(GameControllerMessageType.CLIENT_MOOD_CHANGE)
public final class ClientMoodChangeController implements MessageQueueController<MessageQueueData> {
    @Override
    public void handleIncoming(GameRequestContext context, ServerObject actor, int flags, float value, MessageQueueData data) {
        if (value < 0 || value > 255.0f) {
            LOGGER.warn("Invalid mood requested with value of {} by object {}.", value, actor.getNetworkId());
            return;
        }

        final CreatureObject creo = actor.asCreatureObject();

        if (creo != null) {
            LOGGER.warn("set mood is not yet implemented on creo.");
            //creo.setMood(value);
        }
    }
}
