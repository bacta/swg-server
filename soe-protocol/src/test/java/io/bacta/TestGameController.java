package io.bacta;

import io.bacta.login.message.LoginClientId;
import io.bacta.soe.network.connection.ClientConnection;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import org.springframework.stereotype.Component;

@Component
@MessageHandled(handles = LoginClientId.class)
@ConnectionRolesAllowed({})
public class TestGameController implements GameNetworkMessageController<ClientConnection, LoginClientId> {

    @Override
    public void handleIncoming(ClientConnection connection, LoginClientId message) throws Exception {

    }
}
