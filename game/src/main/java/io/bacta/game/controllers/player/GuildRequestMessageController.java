package io.bacta.game.controllers.player;

import io.bacta.game.context.GameRequestContext;
import io.bacta.game.message.player.GuildRequestMessage;
import io.bacta.game.message.player.GuildResponseMessage;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@MessageHandled(handles = GuildRequestMessage.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class GuildRequestMessageController implements GameNetworkMessageController<GameRequestContext, GuildRequestMessage> {
    @Override
    public void handleIncoming(GameRequestContext context, GuildRequestMessage message) throws Exception {
        LOGGER.warn("This controller is not fully implemented");

        //Get the player creature
        //Get the guild id from the player creature.
        //Lookup the guild from the guild service by guild id.
        //Get the name and title from the guild info.
        //respond with the id, name, title
        final long id = 0;
        final String name = "";
        final String title = "";

        final GuildResponseMessage response = new GuildResponseMessage(id, name, title);
        context.sendMessage(response);
    }
}

