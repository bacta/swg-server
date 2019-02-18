package io.bacta.game.controllers.connection;

import io.bacta.game.GameServerProperties;
import io.bacta.game.message.connection.ClientIdMsg;
import io.bacta.game.message.connection.ClientPermissionsMessage;
import io.bacta.soe.context.SoeRequestContext;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Slf4j
@Component
@MessageHandled(handles = ClientIdMsg.class)
@ConnectionRolesAllowed({})
public class ClientIdMsgController implements GameNetworkMessageController<SoeRequestContext, ClientIdMsg> {
    private final String requiredClientVersion;

    @Inject
    public ClientIdMsgController(final GameServerProperties properties) {
        requiredClientVersion = properties.getRequiredClientVersion();
    }
    
    @Override
    public void handleIncoming(SoeRequestContext context, ClientIdMsg message) throws Exception {

//        ErrorMessage error = new ErrorMessage("Login Error", "Not implemented.", false);
//        connection.sendMessage(error);

        // Validate client version
        /* (!message.getClientVersion().equals(requiredClientVersion)) {
            ErrorMessage error = new ErrorMessage("Login Error", "The client you are attempting to connect with does not match that required by the server.", false);
            connection.sendMessage(error);
            logger.info("Sending Client Error");
            return;
        }

        SoeAccount account = accountService.validateSession(message.getToken());
        if (account == null) {
            ErrorMessage error = new ErrorMessage("Error", "Invalid Session", false);
            connection.sendMessage(error);
            logger.info("Invalid Session: " + message.getToken());
            return;
        }

        connection.setBactaId(account.getId());
        connection.setBactaUsername(account.getUsername());
        */

        context.addRole(ConnectionRole.AUTHENTICATED);

        // TODO: Actually implement permissions
        ClientPermissionsMessage cpm = new ClientPermissionsMessage(true, true, true, true);
        context.sendMessage(cpm);
    }
}

