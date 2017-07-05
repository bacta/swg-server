package com.ocdsoft.bacta.swg.login.service;

import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.network.message.login.LoginIncorrectClientId;
import com.ocdsoft.bacta.soe.network.message.login.ServerNowEpochTime;
import com.ocdsoft.bacta.swg.login.LoginServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by crush on 7/3/2017.
 */
@Slf4j
@Service
public final class DefaultClientService implements ClientService {
    private final SessionService sessionService;

    private final List<SoeUdpConnection> validatedClients;
    private final LoginServerProperties loginServerProperties;

    @Inject
    public DefaultClientService(SessionService sessionService, LoginServerProperties loginServerProperties) {
        this.sessionService = sessionService;
        this.loginServerProperties = loginServerProperties;

        this.validatedClients = new ArrayList<>();
    }

    @Override
    public void validateClient(SoeUdpConnection connection, String clientVersion, String id, String key) {
        //Send the client the server "now" epoch time so that the client has an idea
        //of how much difference there is between the client's epoch time and the server epoch time.
        final ServerNowEpochTime serverEpoch = new ServerNowEpochTime((int) (System.currentTimeMillis() / 1000));
        connection.sendMessage(serverEpoch);

        if (!validateClientVersion(connection, clientVersion))
            return;

        //First try to validate the key, assuming it as a session key.
        //If that fails, then fallback to trying username/password.
        if (!sessionService.validate(connection, key)) {
            if (!sessionService.login(connection, id, key)) {
                //Failed to identify. Reject them.
            }
        }

        //Login with id/key based on login type:
        //- Session Validate: means that the id is the requestedAdminSuid and key is the session token from launchpad.
        //- Session Login: means that we don't have a session yet. id is the username, key is the password.
        //otherwise, treat id as the bactaId. If it's not a number, then hash it.

        //clientValidated(connection, suid, id, null, true, 0xFFFFFF, 0xFFFFFF);
    }

    @Override
    public void clientValidated(SoeUdpConnection connection, int bactaId, String username, String sessionKey, boolean isSecure, int gameBits, int subscriptionBits) {
        //implement admin
    }

    private boolean validateClientVersion(SoeUdpConnection connection, String clientVersion) {
        //TODO: In the future, we might want to change up how versions are validated.
        if (loginServerProperties.isValidateClientVersionEnabled()
                && loginServerProperties.getRequiredClientVersion().equals(clientVersion)) {

            LOGGER.warn("Client {} tried to login with version {} but {} was required.",
                    connection.getRemoteAddress(),
                    clientVersion,
                    loginServerProperties.getRequiredClientVersion());

            final LoginIncorrectClientId incorrectId = new LoginIncorrectClientId("", "");
            connection.sendMessage(incorrectId);

            return false;
        }

        return true;
    }
}
