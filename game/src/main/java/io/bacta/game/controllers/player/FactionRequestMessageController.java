package io.bacta.game.controllers.player;

import gnu.trove.list.TFloatList;
import gnu.trove.list.array.TFloatArrayList;
import io.bacta.game.context.GameRequestContext;
import io.bacta.game.message.player.FactionRequestMessage;
import io.bacta.game.message.player.FactionResponseMessage;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@MessageHandled(handles = FactionRequestMessage.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public final class FactionRequestMessageController implements GameNetworkMessageController<GameRequestContext, FactionRequestMessage> {
    @Override
    public void handleIncoming(GameRequestContext context, FactionRequestMessage message) throws Exception {
        LOGGER.warn("This controller is not fully implemented");

        //Need to get teh player creature from the context.

        //Get the factions from a faction service.
        //Apparently this stuff was stored on an obj var called "factions" on the CREO.
        final int rebel = 0;
        final int imperial = 0;
        final int criminal = 0;

        final List<String> factionNames = Collections.emptyList();
        final TFloatList factionValues = new TFloatArrayList();

        final FactionResponseMessage responseMessage = new FactionResponseMessage(rebel, imperial, criminal, factionNames, factionValues);
        context.sendMessage(responseMessage);
    }
}

