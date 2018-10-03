package io.bacta.game.controllers.object;

import io.bacta.game.GameControllerMessage;
import io.bacta.game.GameControllerMessageType;
import io.bacta.game.context.GameRequestContext;
import io.bacta.game.message.object.DataTransformWithParent;
import io.bacta.game.object.ServerObject;
import io.bacta.game.object.ServerObjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
@GameControllerMessage(GameControllerMessageType.NET_UPDATE_TRANSFORM_WITH_PARENT)
public class DataTransformWithParentController implements MessageQueueController<DataTransformWithParent> {
    private final ServerObjectService serverObjectService;

    @Inject
    public DataTransformWithParentController(final ServerObjectService serverObjectService) {
        this.serverObjectService = serverObjectService;
    }

    @Override
    public void handleIncoming(GameRequestContext context, ServerObject actor, int flags, float value, DataTransformWithParent data) {
        LOGGER.warn("This controller is not implemented");
    }
}
