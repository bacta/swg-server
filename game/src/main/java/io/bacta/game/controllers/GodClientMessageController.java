package io.bacta.game.controllers;

import io.bacta.game.message.GodClientMessage;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@MessageHandled(handles = GodClientMessage.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class GodClientMessageController implements GameNetworkMessageController<GodClientMessage> {
    @Override
    public void handleIncoming(SoeConnection connection, GodClientMessage message) throws Exception {
        LOGGER.warn("This controller is not implemented");
    }
}

