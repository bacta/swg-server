package io.bacta.game.controllers;

import io.bacta.game.context.GameRequestContext;
import io.bacta.game.message.ConnectPlayerMessage;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@MessageHandled(handles = ConnectPlayerMessage.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class ConnectPlayerMessageController implements GameNetworkMessageController<GameRequestContext, ConnectPlayerMessage> {
    @Override
    public void handleIncoming(GameRequestContext context, ConnectPlayerMessage message) throws Exception {
        LOGGER.warn("This controller is not implemented");
    }
}

