package io.bacta.game.controllers;

import io.bacta.game.context.GameRequestContext;
import io.bacta.game.message.GalaxyLoopTimesResponse;
import io.bacta.game.message.RequestGalaxyLoopTimes;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@MessageHandled(handles = RequestGalaxyLoopTimes.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class RequestGalaxyLoopTimesController implements GameNetworkMessageController<GameRequestContext, RequestGalaxyLoopTimes> {
    @Override
    public void handleIncoming(GameRequestContext context, RequestGalaxyLoopTimes message) throws Exception {
        // TODO: Frame millis, if possible
        context.sendMessage(new GalaxyLoopTimesResponse(0, 0));
    }
}

