package io.bacta.game.controllers.object;

import io.bacta.game.GameControllerMessage;
import io.bacta.game.GameControllerMessageType;
import io.bacta.game.message.object.NetUpdateTransform;
import io.bacta.game.object.ServerObject;
import io.bacta.soe.context.SoeRequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@GameControllerMessage(GameControllerMessageType.NET_UPDATE_TRANSFORM)
public class NetUpdateTransformController implements MessageQueueController<NetUpdateTransform> {

	@Override
	public void handleIncoming(SoeRequestContext context, ServerObject actor, int flags, float value, NetUpdateTransform data) {
		LOGGER.warn("This object controller is not implemented");
	}
}
