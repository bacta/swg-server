package io.bacta.login.server;

import io.bacta.soe.context.SoeRequestContext;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import org.springframework.stereotype.Component;

@Component
@MessageHandled(handles = ImplicitConnectionTestMessage.class)
@ConnectionRolesAllowed({})
public final class ImplicitConnectionTestController implements GameNetworkMessageController<SoeRequestContext, ImplicitConnectionTestMessage> {

    @Override
    public void handleIncoming(SoeRequestContext context, ImplicitConnectionTestMessage message) throws Exception {

    }
}
