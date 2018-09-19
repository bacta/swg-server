package io.bacta.game.controllers.object;


import io.bacta.game.GameControllerMessage;
import io.bacta.game.GameControllerMessageType;
import io.bacta.game.context.GameRequestContext;
import io.bacta.game.message.object.DataTransform;
import io.bacta.game.object.ServerObject;
import io.bacta.game.object.ServerObjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
@GameControllerMessage(GameControllerMessageType.NET_UPDATE_TRANSFORM)
public class DataTransformController implements MessageQueueController<DataTransform> {
    private final ServerObjectService serverObjectService;

    @Inject
    public DataTransformController(final ServerObjectService serverObjectService) {
        this.serverObjectService = serverObjectService;
    }

    @Override
    public void handleIncoming(GameRequestContext context, ServerObject actor, int flags, float value, DataTransform data) {
        LOGGER.warn("This controller is not implemented");
    }
}
