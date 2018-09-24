package io.bacta.game.controllers;

import io.bacta.game.context.GameRequestContext;
import io.bacta.game.message.GcwGroupsRsp;
import io.bacta.game.message.GcwRegionsReq;
import io.bacta.game.message.GcwRegionsRsp;
import io.bacta.game.pvp.GcwService;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
@MessageHandled(handles = GcwRegionsReq.class)
@ConnectionRolesAllowed({ConnectionRole.AUTHENTICATED})
public final class GcwRegionsReqController implements GameNetworkMessageController<GameRequestContext, GcwRegionsReq> {
    private final GcwService gcwService;

    @Inject
    public GcwRegionsReqController(GcwService gcwService) {
        this.gcwService = gcwService;
    }

    @Override
    public void handleIncoming(GameRequestContext context, GcwRegionsReq message) throws Exception {
        gcwService.getGcwScoreCategoryRegions();
        gcwService.getGcwScoreCategoryGroups();

        final GcwRegionsRsp regionsResponse = new GcwRegionsRsp();
        context.sendMessage(regionsResponse);

        final GcwGroupsRsp groupsResponse = new GcwGroupsRsp();
        context.sendMessage(groupsResponse);

        //send GcwRegionsRsp
        //send GcwGroupsRsp

    }
}
