package io.bacta.game.controllers;

import io.bacta.game.message.CommoditiesItemTypeListRequest;
import io.bacta.soe.context.SoeRequestContext;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@MessageHandled(handles = CommoditiesItemTypeListRequest.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class CommoditiesItemTypeListRequestController implements GameNetworkMessageController<SoeRequestContext, CommoditiesItemTypeListRequest> {
    @Override
    public void handleIncoming(SoeRequestContext context, CommoditiesItemTypeListRequest message) throws Exception {
        LOGGER.warn("This controller is not implemented");
    }
}

