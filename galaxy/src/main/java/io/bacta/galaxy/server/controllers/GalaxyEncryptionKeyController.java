package io.bacta.galaxy.server.controllers;

import io.bacta.login.message.GalaxyEncryptionKey;
import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@MessageHandled(handles = GalaxyEncryptionKey.class)
@ConnectionRolesAllowed({})
public final class GalaxyEncryptionKeyController implements GameNetworkMessageController<SoeConnection, GalaxyEncryptionKey> {
    @Override
    public void handleIncoming(SoeConnection connection, GalaxyEncryptionKey message) throws Exception {
        LOGGER.info("Received encryption key.");
    }
}
