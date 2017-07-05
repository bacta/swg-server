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

package bacta.io.login.server.service;

import bacta.io.login.server.LoginServerProperties;
import bacta.io.soe.network.connection.SoeUdpConnection;
import io.bacta.login.message.LoginIncorrectClientId;
import io.bacta.login.message.ServerNowEpochTime;
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
