package io.bacta.game.controllers.connection;

import io.bacta.game.config.GameServerProperties;
import io.bacta.game.message.ErrorMessage;
import io.bacta.game.message.connection.ClientIdMsg;
import io.bacta.game.message.connection.ClientPermissionsMessage;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.soe.network.controller.ConnectionRolesAllowed;
import io.bacta.soe.network.controller.GameNetworkMessageController;
import io.bacta.soe.network.controller.MessageHandled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.inject.Inject;

@Component
@MessageHandled(handles = ClientIdMsg.class)
@ConnectionRolesAllowed({})
public class ClientIdMsgController implements GameNetworkMessageController<ClientIdMsg> {

    private static Logger logger = LoggerFactory.getLogger(ClientIdMsgController.class);

    private final AccountService<SoeAccount> accountService;
    private final String requiredClientVersion;

    @Inject
    public ClientIdMsgController(final AccountService<SoeAccount> accountService,
                                 final GameServerProperties properties) {
        this.accountService = accountService;
        requiredClientVersion = properties.getRequiredClientVersion();
    }
    
    @Override
    public void handleIncoming(SoeConnection connection, ClientIdMsg message) throws Exception {

        // Validate client version
        if (!message.getClientVersion().equals(requiredClientVersion)) {
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
        connection.addRole(ConnectionRole.AUTHENTICATED);

        // TODO: Actually implement permissions
        ClientPermissionsMessage cpm = new ClientPermissionsMessage(true, true, true, true);
        connection.sendMessage(cpm);
    }
}

