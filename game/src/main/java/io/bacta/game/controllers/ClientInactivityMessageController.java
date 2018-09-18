package io.bacta.game.controllers;

import io.bacta.game.context.GameRequestContext;
import io.bacta.game.message.ClientInactivityMessage;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@MessageHandled(handles = ClientInactivityMessage.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class ClientInactivityMessageController implements GameNetworkMessageController<GameRequestContext, ClientInactivityMessage> {
    @Override
    public void handleIncoming(GameRequestContext context, ClientInactivityMessage message) throws Exception {
        LOGGER.warn("This controller is not implemented");

        //If they went inactive: {
        //Check the timestamp of their last active time. If it's greater than zero, then process.
        //Get the difference between now and their last active play time. Add this to their total active play time for the session.
        //Set the last active play timestamp to 0
        //update the play time on the game server for the character
        //check if we want to drop inactive players or not. If yes, drop them.
        //}

        //If they went active: {
        //check that their last active play timestamp is 0. If yes, then process
        //set their last active play time to this timestamp
        //update the play time on the game server
        //}
    }
}

