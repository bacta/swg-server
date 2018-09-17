package io.bacta.game.controllers;

import io.bacta.game.ObjControllerMessage;
import io.bacta.game.context.GameRequestContext;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@MessageHandled(handles = ObjControllerMessage.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class ObjControllerMessageController implements GameNetworkMessageController<GameRequestContext, ObjControllerMessage> {
    @Override
    public void handleIncoming(GameRequestContext context, ObjControllerMessage message) throws Exception {
        LOGGER.info("Handling objc");
    }
}
