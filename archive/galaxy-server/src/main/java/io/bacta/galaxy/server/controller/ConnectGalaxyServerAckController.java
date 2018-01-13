package io.bacta.galaxy.server.controller;

import io.bacta.login.message.ConnectGalaxyServerAck;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.soe.network.controller.*;
import org.springframework.stereotype.Component;

/**
 * Created by kyle on 7/7/2017.
 */
@Component
@MessageHandled(handles = ConnectGalaxyServerAck.class)
@ControllerLifecycle(stagesLoaded = ControllerLifecycleStage.STARTING)
@ConnectionRolesAllowed({})
public class ConnectGalaxyServerAckController implements GameNetworkMessageController<ConnectGalaxyServerAck> {


    @Override
    public void handleIncoming(SoeConnection connection, GameNetworkMessage message) throws Exception {

    }
}
