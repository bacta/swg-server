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

import io.bacta.login.message.LoginClientToken;
import io.bacta.login.message.LoginIncorrectClientId;
import io.bacta.login.message.ServerNowEpochTime;
import io.bacta.login.server.LoginServerProperties;
import io.bacta.session.client.SessionClient;
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
    private final CharacterService characterService;
    private final ClusterService clusterService;
    private final SessionClient sessionClient;

    //TODO: Is this the best way to get this value!?
    private final String requiredClientVersion;

    @Inject
    public DefaultClientService(LoginServerProperties loginServerProperties,
                                CharacterService characterService,
                                ClusterService clusterService,
                                SessionClient sessionClient,
                                @Value("${bacta.network.shared.requiredClientVersion}") String requiredClientVersion) {
        this.loginServerProperties = loginServerProperties;
        this.characterService = characterService;
        this.clusterService = clusterService;
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
                establishSessionMode(connection, id, key);
                break;
            }
            case VALIDATE: {
                validateSessionMode(connection, key);
                break;
            }
            case DISCOVER:
            default: {
                discoverSessionMode(connection, id, key);
                break;
            }
        }
    }

    @Override
    public void clientValidated(SoeUdpConnection connection, int bactaId, String username, String sessionKey, boolean isSecure, int gameBits, int subscriptionBits) {
        //implement admin

        //Create a key with:
        //If they logged in with a session, then the key is comprised of the sessionId + accountId
        //Otherwise, the key is comprised of the accountId, if they are connecting with god client, and username
        //Encrypt the key before sending to client (not sure why, we've already exposed the sessionId as plain text)

        final String token = "hello world";
        sendLoginClientToken(connection, token, bactaId, username);

        clusterService.sendClusterEnum(connection);
        clusterService.sendDisabledCharacterCreationServers(connection);
        characterService.sendEnumerateCharacters(connection, bactaId);
        clusterService.sendClusterStatus(connection);
    }

    private void sendLoginClientToken(SoeUdpConnection connection, String token, int bactaId, String username) {
        final LoginClientToken message = new LoginClientToken(token, bactaId, username);
        connection.sendMessage(message);
    }

    private void establishSessionMode(SoeUdpConnection connection, String username, String password) {
        //final Session session = sessionClient.establish(username, password);
        clientValidated(connection, 1, username, password, false, 0, 0);
    }

    private void validateSessionMode(SoeUdpConnection connection, String sessionKey) {
        //final Session session = sessionClient.validate(sessionKey);
    }

    private void discoverSessionMode(SoeUdpConnection connection, String id, String key) {
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
