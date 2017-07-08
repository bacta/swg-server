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

package io.bacta.login.server.service;

import io.bacta.login.message.LoginIncorrectClientId;
import io.bacta.login.message.ServerNowEpochTime;
import io.bacta.login.server.LoginServerProperties;
import io.bacta.session.client.SessionClient;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.connection.SoeUdpConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Created by crush on 7/3/2017.
 */
@Slf4j
@Service
public final class DefaultClientService implements ClientService {
    private final LoginServerProperties loginServerProperties;
    private final SessionClient sessionClient;

    //TODO: Is this the best way to get this value!?
    private final String requiredClientVersion;

    @Inject
    public DefaultClientService(LoginServerProperties loginServerProperties,
                                SessionClient sessionClient,
                                @Value("${bacta.network.shared.requiredClientVersion}") String requiredClientVersion) {
        this.loginServerProperties = loginServerProperties;
        this.sessionClient = sessionClient;
        this.requiredClientVersion = requiredClientVersion;
    }

    @Override
    public void validateClient(SoeUdpConnection connection, String clientVersion, String id, String key) {
        //Client wants to know the difference in time between the server and client.
        final int epoch = (int) (System.currentTimeMillis() / 1000);

        LOGGER.info("Sending server epoch {} to client {}.", epoch, connection.getRemoteAddress());

        final ServerNowEpochTime serverEpoch = new ServerNowEpochTime(epoch);
        connection.sendMessage(serverEpoch);

        if (!validateClientVersion(connection, clientVersion))
            return;

        switch (loginServerProperties.getSessionMode()) {
            case ESTABLISH: {
                establishSessionMode(id, key);
                break;
            }
            case VALIDATE: {
                validateSessionMode(key);
                break;
            }
            case DISCOVER:
            default: {
                discoverSessionMode(id, key);
                break;
            }
        }

        connection.addRole(ConnectionRole.AUTHENTICATED);
    }

    @Override
    public void clientValidated(SoeUdpConnection connection, int bactaId, String username, String sessionKey, boolean isSecure, int gameBits, int subscriptionBits) {
        //implement admin

        //Create a key with:
        //If they logged in with a session, then the key is comprised of the sessionId + accountId
        //Otherwise, the key is comprised of the accountId, if they are connecting with god client, and username
        //Encrypt the key with the keyshare.
        //Encrypting the token is for the connection server's protection. If the connection server can't decrypt the
        //token, then it would mean it came from an untrusted login server.



        //Send LoginClientToken (token, accountId, username);
        //Send LoginEnumCluster
        //Send CharacterCreationDisabled
        //Send EnumerateCharacterId
        //Send LoginClusterStatus
    }

    private void establishSessionMode(final String username, final String password) {
        //final Session session = sessionClient.establish(username, password);
    }

    private void validateSessionMode(final String sessionKey) {
        //final Session session = sessionClient.validate(sessionKey);
    }

    private void discoverSessionMode(final String id, final String key) {
    }

    private boolean validateClientVersion(SoeUdpConnection connection, String clientVersion) {
        //TODO: In the future, we might want to change up how versions are validated.
        if (loginServerProperties.isValidateClientVersionEnabled()
                && requiredClientVersion.equals(clientVersion)) {

            LOGGER.warn("Client {} tried to establish with version {} but {} was required.",
                    connection.getRemoteAddress(),
                    clientVersion,
                    requiredClientVersion);

            final LoginIncorrectClientId incorrectId = new LoginIncorrectClientId("", "");
            connection.sendMessage(incorrectId);

            return false;
        }

        return true;
    }
}
