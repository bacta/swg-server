package io.bacta.game.controllers.object;

import io.bacta.game.GameControllerMessage;
import io.bacta.game.GameControllerMessageType;
import io.bacta.game.context.GameRequestContext;
import io.bacta.game.message.object.MessageQueueSecureTrade;
import io.bacta.game.object.ServerObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@GameControllerMessage(GameControllerMessageType.SECURE_TRADE)
public final class SecureTradeController implements MessageQueueController<MessageQueueSecureTrade> {
    @Override
    public void handleIncoming(GameRequestContext context, ServerObject actor, int flags, float value, MessageQueueSecureTrade data) {
        LOGGER.warn("Not yet implemented.");
    }
}
