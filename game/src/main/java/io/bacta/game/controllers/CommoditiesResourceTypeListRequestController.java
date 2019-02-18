package io.bacta.game.controllers;

import io.bacta.game.message.CommoditiesResourceTypeListRequest;
import io.bacta.soe.context.SoeRequestContext;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@MessageHandled(handles = CommoditiesResourceTypeListRequest.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class CommoditiesResourceTypeListRequestController implements GameNetworkMessageController<SoeRequestContext, CommoditiesResourceTypeListRequest> {
    @Override
    public void handleIncoming(SoeRequestContext context, CommoditiesResourceTypeListRequest message) throws Exception {
        LOGGER.warn("This controller is not implemented");
    }
}

