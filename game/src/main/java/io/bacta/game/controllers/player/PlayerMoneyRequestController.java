package io.bacta.game.controllers.player;

import io.bacta.game.context.GameRequestContext;
import io.bacta.game.message.player.PlayerMoneyRequest;
import io.bacta.game.message.player.PlayerMoneyResponse;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@MessageHandled(handles = PlayerMoneyRequest.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class PlayerMoneyRequestController implements GameNetworkMessageController<GameRequestContext, PlayerMoneyRequest> {
    @Override
    public void handleIncoming(GameRequestContext context, PlayerMoneyRequest message) throws Exception {
        LOGGER.warn("This controller is not implemented");

        //Once the character object can be obtained from the context we can finish this properly.
        final PlayerMoneyResponse response = new PlayerMoneyResponse(0, 0);
        context.sendMessage(response);
    }
}

