package io.bacta.galaxy.server.controllers;

import io.bacta.galaxy.server.service.GalaxyIdentityService;
import io.bacta.login.message.LoginServerOnline;
import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@MessageHandled(handles = LoginServerOnline.class)
@ConnectionRolesAllowed({})
public final class LoginServerOnlineController implements GameNetworkMessageController<SoeConnection, LoginServerOnline> {
    private final GalaxyIdentityService galaxyIdentityService;

    @Inject
    public LoginServerOnlineController(GalaxyIdentityService galaxyIdentityService) {
        this.galaxyIdentityService = galaxyIdentityService;
    }

    @Override
    public void handleIncoming(SoeConnection connection, LoginServerOnline message) throws Exception {
        galaxyIdentityService.sendGalaxyServerId(connection);
    }
}
