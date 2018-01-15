package io.bacta.galaxy.server.controllers;

import io.bacta.login.message.GalaxyServerIdAck;
import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import org.springframework.stereotype.Component;

@Component
@MessageHandled(handles = GalaxyServerIdAck.class)
@ConnectionRolesAllowed({})
public class GalaxyServerIdAckController implements GameNetworkMessageController<SoeConnection, GalaxyServerIdAck> {
    @Override
    public void handleIncoming(SoeConnection connection, GalaxyServerIdAck message) throws Exception {

    }
}
