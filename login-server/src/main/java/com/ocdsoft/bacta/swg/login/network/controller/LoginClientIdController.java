package com.ocdsoft.bacta.swg.login.network.controller;


import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.network.controller.ConnectionRolesAllowed;
import com.ocdsoft.bacta.soe.network.controller.GameNetworkMessageController;
import com.ocdsoft.bacta.soe.network.controller.MessageHandled;
import com.ocdsoft.bacta.soe.network.message.login.LoginClientId;
import com.ocdsoft.bacta.swg.login.service.ClientService;
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

