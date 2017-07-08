package io.bacta.galaxy.server.controller;

import io.bacta.login.message.RegisterGalaxyAck;
import io.bacta.soe.network.connection.SoeUdpConnection;
import io.bacta.soe.network.controller.*;
import org.springframework.stereotype.Component;

/**
 * Created by kyle on 7/7/2017.
 */
@Component
@MessageHandled(handles = RegisterGalaxyAck.class)
@ControllerLifecycle(stagesLoaded = ControllerLifecycleStage.STARTING)
@ConnectionRolesAllowed({})
public class RegisterGalaxyAckController implements GameNetworkMessageController<RegisterGalaxyAck> {

    @Override
    public void handleIncoming(SoeUdpConnection connection, RegisterGalaxyAck message) throws Exception {

    }
}
