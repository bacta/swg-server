package io.bacta.game.controllers.command;

import io.bacta.game.context.GameRequestContext;
import io.bacta.game.controllers.object.CommandQueueController;
import io.bacta.game.controllers.object.QueuesCommand;
import io.bacta.game.controllers.player.CharacterSheetResponseMessage;
import io.bacta.game.object.ServerObject;
import io.bacta.shared.math.Vector;
import org.springframework.stereotype.Component;

@Component
@QueuesCommand("requestcharactersheetinfo")
public class RequestCharacterSheetInfoCommandController implements CommandQueueController {
    @Override
    public void handleCommand(GameRequestContext context, ServerObject actor, ServerObject target, String params) {

        final int bornDate = 0;
        final int played = 0;

        final Vector bindLocation = Vector.ZERO;
        final String bindPlanet = "";

        final Vector bankLocation = Vector.ZERO;
        final String bankPlanet = "";

        final Vector residenceLocation = Vector.ZERO;
        final String residencePlanet = "";

        final String citizensOf = "";

        final String spouseName = "";

        final int lots = 10; //Get max lots from the CREO, and then get used lots from the PLAY. Subtract them and put the diff here.

        final CharacterSheetResponseMessage response = new CharacterSheetResponseMessage(
                bornDate,
                played,
                bindLocation,
                bindPlanet,
                bankLocation,
                bankPlanet,
                residenceLocation,
                residencePlanet,
                citizensOf,
                spouseName,
                lots);

        context.sendMessage(response);
    }
}
