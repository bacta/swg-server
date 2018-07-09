package io.bacta.login.server.controller;

import io.bacta.galaxy.message.GalaxyServerStatus;
import io.bacta.login.server.service.GalaxyService;
import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@MessageHandled(handles = GalaxyServerStatus.class)
@ConnectionRolesAllowed({})
public final class GalaxyServerStatusController implements GameNetworkMessageController<GalaxyServerStatus> {
    private final GalaxyService galaxyService;

    @Inject
    public GalaxyServerStatusController(GalaxyService galaxyService) {
        this.galaxyService = galaxyService;
    }


    @Override
    public void handleIncoming(SoeConnection connection, GalaxyServerStatus message) throws Exception {
        this.galaxyService.updateGalaxyStatus(connection, message);
    }
}
