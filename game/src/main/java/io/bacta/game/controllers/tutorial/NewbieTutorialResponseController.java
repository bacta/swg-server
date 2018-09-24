package io.bacta.game.controllers.tutorial;

import io.bacta.game.context.GameRequestContext;
import io.bacta.game.message.tutorial.NewbieTutorialResponse;
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
public class NewbieTutorialResponseController implements GameNetworkMessageController<GameRequestContext, NewbieTutorialResponse> {
    @Override
    public void handleIncoming(GameRequestContext context, NewbieTutorialResponse message) throws Exception {
        LOGGER.warn("This controller is not fully implemented");

        final String response = message.getResponse();

        LOGGER.info("Received newbie tutorial response of {}", response);
    }
}
