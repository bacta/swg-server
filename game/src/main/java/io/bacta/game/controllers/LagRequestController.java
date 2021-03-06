package io.bacta.game.controllers;

import io.bacta.game.message.LagRequest;
import io.bacta.soe.context.SoeRequestContext;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@MessageHandled(handles = LagRequest.class)
@ConnectionRolesAllowed({})
public class LagRequestController implements GameNetworkMessageController<SoeRequestContext, LagRequest> {
    @Override
    public void handleIncoming(SoeRequestContext context, LagRequest message) throws Exception {
        LOGGER.warn("This controller is not implemented");
    }
}

