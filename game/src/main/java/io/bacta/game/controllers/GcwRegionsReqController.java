package io.bacta.game.controllers;

import io.bacta.game.message.GcwRegionsReq;
import io.bacta.soe.context.SoeRequestContext;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@MessageHandled(handles = GcwRegionsReq.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public class GcwRegionsReqController implements GameNetworkMessageController<SoeRequestContext, GcwRegionsReq> {
    @Override
    public void handleIncoming(SoeRequestContext context, GcwRegionsReq message) throws Exception {
        LOGGER.warn("This controller is not implemented");
    }
}

