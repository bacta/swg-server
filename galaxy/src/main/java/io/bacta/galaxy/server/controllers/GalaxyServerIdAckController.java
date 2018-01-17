package io.bacta.galaxy.server.controllers;

import io.bacta.login.message.GalaxyServerIdAck;
import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@MessageHandled(handles = GalaxyServerIdAck.class)
@ConnectionRolesAllowed({})
public class GalaxyServerIdAckController implements GameNetworkMessageController<SoeConnection, GalaxyServerIdAck> {
    @Override
    public void handleIncoming(SoeConnection connection, GalaxyServerIdAck message) throws Exception {
        LOGGER.info("Received ack message with galaxy id {}", message.getGalaxyId());
    }
}
