package io.bacta.login.server;

import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import org.springframework.stereotype.Component;

@Component
@MessageHandled(handles = ImplicitConnectionTestMessage.class)
@ConnectionRolesAllowed({})
public final class ImplicitConnectionTestController implements GameNetworkMessageController<ImplicitConnectionTestMessage> {

    @Override
    public void handleIncoming(SoeConnection connection, ImplicitConnectionTestMessage message) throws Exception {

    }
}
