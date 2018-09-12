package io.bacta.login.server.controller;

import io.bacta.galaxy.message.GalaxyServerId;
import io.bacta.login.server.service.GalaxyService;
import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@MessageHandled(handles = GalaxyServerId.class)
@ConnectionRolesAllowed({})
public final class GalaxyServerIdController implements GameNetworkMessageController<GalaxyServerId> {
    private final GalaxyService galaxyService;

    @Inject
    public GalaxyServerIdController(GalaxyService galaxyService) {
        this.galaxyService = galaxyService;
    }

    @Override
    public void handleIncoming(SoeConnection connection, GalaxyServerId message) throws Exception {
        //this.galaxyService.identifyGalaxy(connection, message.getGalaxyName(), message.getTimeZone());
    }
}