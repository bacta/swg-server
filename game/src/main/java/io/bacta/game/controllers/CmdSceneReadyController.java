package io.bacta.game.controllers;

import io.bacta.game.context.GameRequestContext;
import io.bacta.game.message.CmdSceneReady;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@MessageHandled(handles = CmdSceneReady.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class CmdSceneReadyController implements GameNetworkMessageController<GameRequestContext, CmdSceneReady> {
    @Override
    public void handleIncoming(GameRequestContext context, CmdSceneReady message) throws Exception {
        LOGGER.warn("This controller is not implemented");

//        // Make sure our player has the server's speed maximum
//        PlayerCreatureController const * playerController = safe_cast<PlayerCreatureController const *>(getCreatureController());
//        if(playerController)
//        {
//            GenericValueTypeMessage<float> const msg("fca11a62d23041008a4f0df36aa7dca6", playerController->getServerSpeedForPlayer());
//            Client const * const client = getClient();
//            if (client)
//                return client->send(msg, true);
//        }

        //onGroupMemberConnected(getNetworkId())

        //Check cell permissions. Kick out if no longer allowed in cell.
    }
}

