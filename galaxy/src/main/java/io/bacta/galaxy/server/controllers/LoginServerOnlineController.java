package io.bacta.galaxy.server.controllers;

import io.bacta.galaxy.message.GalaxyServerId;
import io.bacta.login.message.LoginServerOnline;
import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import org.springframework.stereotype.Component;

@Component
@MessageHandled(handles = GalaxyServerId.class)
@ConnectionRolesAllowed({})
public final class LoginServerOnlineController implements GameNetworkMessageController<SoeConnection, LoginServerOnline> {
    @Override
    public void handleIncoming(SoeConnection connection, LoginServerOnline message) throws Exception {

    }
}
