package bacta.io.login.server.network.controller;


import bacta.io.login.server.service.ClientService;
import bacta.io.soe.network.connection.SoeUdpConnection;
import bacta.io.soe.network.controller.ConnectionRolesAllowed;
import bacta.io.soe.network.controller.GameNetworkMessageController;
import bacta.io.soe.network.controller.MessageHandled;
import io.bacta.login.message.LoginClientId;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@MessageHandled(handles = LoginClientId.class)
@ConnectionRolesAllowed({})
public class LoginClientIdController implements GameNetworkMessageController<LoginClientId> {
    private final ClientService clientService;

    @Inject
    public LoginClientIdController(final ClientService clientService) {
        this.clientService = clientService;
    }

    @Override
    public void handleIncoming(SoeUdpConnection connection, LoginClientId message) {
        clientService.validateClient(connection, message.getClientVersion(), message.getId(), message.getKey());
    }
}

