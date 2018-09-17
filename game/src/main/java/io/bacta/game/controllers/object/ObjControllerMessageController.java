package io.bacta.game.controllers.object;


import io.bacta.game.ObjControllerMessage;
import io.bacta.game.context.GameRequestContext;
import io.bacta.game.dispatch.MessageQueueDispatcher;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
@MessageHandled(handles = ObjControllerMessage.class)
public final class ObjControllerMessageController implements GameNetworkMessageController<GameRequestContext, ObjControllerMessage> {

    private final MessageQueueDispatcher messageQueueDispatcher;

    @Inject
    public ObjControllerMessageController(final MessageQueueDispatcher messageQueueDispatcher) {
        this.messageQueueDispatcher = messageQueueDispatcher;
    }

    @Override
    public void handleIncoming(GameRequestContext context, ObjControllerMessage message) throws Exception {
        messageQueueDispatcher.dispatch(context, message);
    }
}