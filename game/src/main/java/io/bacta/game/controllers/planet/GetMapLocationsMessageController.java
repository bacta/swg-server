package io.bacta.game.controllers.planet;

import io.bacta.game.context.GameRequestContext;
import io.bacta.game.message.planet.GetMapLocationsMessage;
import io.bacta.game.planet.PlanetService;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
@MessageHandled(handles = GetMapLocationsMessage.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class GetMapLocationsMessageController implements GameNetworkMessageController<GameRequestContext, GetMapLocationsMessage> {
    private final PlanetService planetService;

    @Inject
    public GetMapLocationsMessageController(PlanetService planetService) {
        this.planetService = planetService;
    }

    @Override
    public void handleIncoming(GameRequestContext context, GetMapLocationsMessage message) throws Exception {
        planetService.sendMapLocationsTo(context, message);
    }
}

