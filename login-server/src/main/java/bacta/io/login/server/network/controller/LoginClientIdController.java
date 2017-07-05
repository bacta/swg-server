/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

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

