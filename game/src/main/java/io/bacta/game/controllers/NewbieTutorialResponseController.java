package io.bacta.game.controllers;

import io.bacta.game.message.NewbieTutorialResponse;
import io.bacta.soe.context.SoeRequestContext;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@MessageHandled(handles = NewbieTutorialResponse.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class NewbieTutorialResponseController implements GameNetworkMessageController<SoeRequestContext, NewbieTutorialResponse> {
    @Override
    public void handleIncoming(SoeRequestContext context, NewbieTutorialResponse message) throws Exception {
        LOGGER.warn("This controller is not implemented");
    }
}

