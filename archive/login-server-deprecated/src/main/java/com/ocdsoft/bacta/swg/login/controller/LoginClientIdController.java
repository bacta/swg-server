package com.ocdsoft.bacta.swg.login.controller;

import com.google.inject.Inject;
import com.ocdsoft.bacta.soe.protocol.ServerType;
import com.ocdsoft.bacta.soe.event.ConnectEvent;
import com.ocdsoft.bacta.soe.network.connection.ConnectionRole;
import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.network.controller.ConnectionRolesAllowed;
import com.ocdsoft.bacta.soe.network.controller.GameNetworkMessageController;
import com.ocdsoft.bacta.soe.network.controller.MessageHandled;
import com.ocdsoft.bacta.soe.config.SoeNetworkConfigurationImpl;
import com.ocdsoft.bacta.soe.service.PublisherService;
import com.ocdsoft.bacta.swg.db.AccountService;
import com.ocdsoft.bacta.swg.login.message.EnumerateCharacterId;
import com.ocdsoft.bacta.swg.login.message.LoginClientId;
import com.ocdsoft.bacta.swg.login.message.LoginClientToken;
import com.ocdsoft.bacta.swg.login.service.ClusterService;
import com.ocdsoft.bacta.swg.server.game.message.ErrorMessage;
import com.ocdsoft.bacta.swg.shared.identity.SoeAccount;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@MessageHandled(handles = LoginClientId.class, type = ServerType.LOGIN)
@ConnectionRolesAllowed({})
public class LoginClientIdController implements GameNetworkMessageController<LoginClientId> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginClientIdController.class);

    private final int timezone;
    private final ClusterService clusterService;
    private final AccountService accountService;
    private final SoeNetworkConfigurationImpl configuration;
    private final PublisherService publisherService;

    @Inject
    public LoginClientIdController(final SoeNetworkConfigurationImpl configuration,
                                   final ClusterService clusterService,
                                   final AccountService accountService,
                                   final PublisherService publisherService) {

        this.clusterService = clusterService;
        this.accountService = accountService;
        this.configuration = configuration;
        this.publisherService = publisherService;

        timezone = DateTimeZone.getDefault().getOffset(null) / 1000;
    }

    @Override
    public void handleIncoming(SoeUdpConnection connection, LoginClientId message) {

        // Validate client version
        if (!isRequiredVersion(message.getClientVersion())) {
            ErrorMessage error = new ErrorMessage("Login Error", "The client you are attempting to connect with does not match that required by the server.", false);
            connection.sendMessage(error);
            LOGGER.debug("Client attempted to connect with client version {} when {} is required", message.getClientVersion(), configuration.getRequiredClientVersion());
            return;
        }

        if (message.getKey().isEmpty()) {
            ErrorMessage error = new ErrorMessage("Login Error", "Please enter a password.", false);
            connection.sendMessage(error);
            return;
        }

        SoeAccount account = null;
        try {

            account = accountService.getAccount(message.getId());

        } catch (Exception e) {
            ErrorMessage error = new ErrorMessage("Login Error", "Duplicate Accounts in the database", false);
            connection.sendMessage(error);
            LOGGER.error("Duplicate accounts in database", e);

            return;
        }

        if (account == null) {
            account = accountService.createAccount(message.getId(), message.getKey());
            if (account == null) {
                ErrorMessage error = new ErrorMessage("Login Error", "Unable to create account.", false);
                connection.sendMessage(error);
                return;
            }
        } else if (!accountService.authenticate(account, message.getKey())) {
            ErrorMessage error = new ErrorMessage("Login Error", "Invalid username or password.", false);
            connection.sendMessage(error);
            return;
        }

        accountService.createAuthToken(connection.getRemoteAddress().getAddress(), account);
        connection.setBactaId(account.getId());
        connection.setAccountUsername(account.getUsername());

        LoginClientToken token = new LoginClientToken(account.getAuthToken(), account.getId(), account.getUsername());
        connection.sendMessage(token);

        clusterService.sendClusterData(connection);

        EnumerateCharacterId characters = new EnumerateCharacterId(account);
        connection.sendMessage(characters);

        connection.addRole(ConnectionRole.AUTHENTICATED);
        publisherService.onEvent(new ConnectEvent(connection));
    }

    private boolean isRequiredVersion(String clientVersion) {
        return clientVersion.equals(configuration.getRequiredClientVersion());
    }
}

