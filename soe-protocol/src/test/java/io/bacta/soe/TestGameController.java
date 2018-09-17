package io.bacta.soe;

import io.bacta.login.message.LoginClientId;
import io.bacta.soe.context.SoeRequestContext;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import org.springframework.stereotype.Component;

@Component
@MessageHandled(handles = LoginClientId.class)
@ConnectionRolesAllowed({})
public class TestGameController implements GameNetworkMessageController<SoeRequestContext, LoginClientId> {

    @Override
    public void handleIncoming(SoeRequestContext context, LoginClientId message) throws Exception {

    }
}
