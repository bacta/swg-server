package io.bacta.game.controllers;

import io.bacta.game.context.GameRequestContext;
import io.bacta.game.message.SetLfgInterests;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@MessageHandled(handles = SetLfgInterests.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public final class SetLfgInterestsController implements GameNetworkMessageController<GameRequestContext, SetLfgInterests> {
    @Override
    public void handleIncoming(GameRequestContext context, SetLfgInterests message) throws Exception {
        LOGGER.warn("Not yet implemented");

        //Get the playerObject from the creature belonging to the context connection.
        //get connected character ldf data
        //find this players lfg data...
        //get the players matchmaking character profile id
        //check if they are looking for group. if so, then set the bit in the incoming bit array.
        //otherwise, clear that bit.

        //do same thing for helper, roleplay, lookingForWork.

        //set connected character interests on the universe server.
    }
}
