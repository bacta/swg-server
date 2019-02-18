package io.bacta.game.controllers.object;

import io.bacta.game.GameControllerMessage;
import io.bacta.game.GameControllerMessageType;
import io.bacta.game.message.object.DataTransform;
import io.bacta.game.object.ServerObject;
import io.bacta.game.service.object.ServerObjectService;
import io.bacta.soe.context.SoeRequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@GameControllerMessage(GameControllerMessageType.NET_UPDATE_TRANSFORM)
public class DataTransformController implements MessageQueueController<DataTransform> {
	private static final Logger LOGGER = LoggerFactory.getLogger(DataTransformController.class);

	private final ServerObjectService serverObjectService;

	@Inject
	public DataTransformController(final ServerObjectService serverObjectService) {
		this.serverObjectService = serverObjectService;
	}

	@Override
	public void handleIncoming(SoeRequestContext context, ServerObject actor, int flags, float value, DataTransform data) {
		LOGGER.warn("This controller is not implemented");
	}
}
