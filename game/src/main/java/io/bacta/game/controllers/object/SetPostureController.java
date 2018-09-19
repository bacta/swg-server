package io.bacta.game.controllers.object;

import io.bacta.game.GameControllerMessage;
import io.bacta.game.GameControllerMessageType;
import io.bacta.game.context.GameRequestContext;
import io.bacta.game.message.object.MessageQueuePosture;
import io.bacta.game.object.ServerObject;
import io.bacta.game.object.tangible.creature.CreatureObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@GameControllerMessage(GameControllerMessageType.SET_POSTURE)
public final class SetPostureController implements MessageQueueController<MessageQueuePosture> {
    @Override
    public void handleIncoming(GameRequestContext context, ServerObject actor, int flags, float value, MessageQueuePosture data) {
        final CreatureObject creo = actor.asCreatureObject();

        if (creo != null) {
            creo.setPosture(data.getPosture());
        }
    }
}
