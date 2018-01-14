package io.bacta.login.server.service;

import io.bacta.login.message.LoginClientToken;
import io.bacta.login.message.LoginIncorrectClientId;
import io.bacta.login.message.ServerNowEpochTime;
import io.bacta.login.server.LoginServerProperties;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.connection.SoeConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * When an SwgClient attempts to connect to the login server, we want to do some validation on their connection before
 * serving them any further.
 */
@Slf4j
@Service
public final class DefaultClientService implements ClientService {
    private final LoginServerProperties loginServerProperties;
    private final CharacterService characterService;
    private final GalaxyService galaxyService;
    private final String requiredClientVersion;

    @Inject
    public DefaultClientService(LoginServerProperties loginServerProperties,
                                CharacterService characterService,
                                GalaxyService galaxyService,
                                @Value("${bacta.network.shared.requiredClientVersion}")
                                 String requiredClientVersion) {
        this.loginServerProperties = loginServerProperties;
        this.characterService = characterService;
        this.galaxyService = galaxyService;
        this.requiredClientVersion = requiredClientVersion;
    }

    @Override
    public void validateClient(SoeConnection connection, String clientVersion, String id, String key) {
        //Client wants to know the difference in time between the server and client.
        final int epoch = (int) (System.currentTimeMillis() / 1000);

        LOGGER.info("Sending server epoch {} to client {}.", epoch, connection.getSoeUdpConnection().getRemoteAddress());

        final ServerNowEpochTime serverEpoch = new ServerNowEpochTime(epoch);
        connection.sendMessage(serverEpoch);

        if (!validateClientVersion(connection, clientVersion))
            return;

        establishSessionMode(connection, id, key);

        /*
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
        */

        connection.addRole(ConnectionRole.AUTHENTICATED);
        connection.addRole(ConnectionRole.LOGIN_CLIENT);
    }


    @Override
    public void clientValidated(SoeConnection connection, int bactaId, String username, String sessionKey, boolean isSecure, int gameBits, int subscriptionBits) {
        //implement admin

        //Create a key with:
        //If they logged in with a session, then the key is comprised of the sessionId + accountId
        //Otherwise, the key is comprised of the accountId, if they are connecting with god client, and username
        //Encrypt the key before sending to client (not sure why, we've already exposed the sessionId as plain text)

        final String token = "hello world";
        sendLoginClientToken(connection, token, bactaId, username);

        galaxyService.sendClusterEnum(connection);
        galaxyService.sendDisabledCharacterCreationServers(connection);
        characterService.sendEnumerateCharacters(connection, bactaId);
        galaxyService.sendClusterStatus(connection);
    }

    private void sendLoginClientToken(SoeConnection connection, String token, int bactaId, String username) {
        final LoginClientToken message = new LoginClientToken(token, bactaId, username);
        connection.sendMessage(message);
    }

    private void establishSessionMode(SoeConnection connection, String username, String password) {
        //final Session session = sessionClient.establish(username, password);
        clientValidated(connection, 1, username, password, false, 0, 0);
    }

    private void validateSessionMode(SoeConnection connection, String sessionKey) {
        //final Session session = sessionClient.validate(sessionKey);
    }

    private void discoverSessionMode(SoeConnection connection, String id, String key) {
    }

    private boolean validateClientVersion(SoeConnection connection, String clientVersion) {
        //TODO: In the future, we might want to change up how versions are validated.
        if (loginServerProperties.isValidateClientVersionEnabled()
                && requiredClientVersion.equals(clientVersion)) {

            LOGGER.warn("Client {} tried to establish with version {} but {} was required.",
                    connection.getSoeUdpConnection().getRemoteAddress(),
                    clientVersion,
                    requiredClientVersion);

            final LoginIncorrectClientId incorrectId = new LoginIncorrectClientId("", "");
            connection.sendMessage(incorrectId);

            return false;
        }

        return true;
    }
}
