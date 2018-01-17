package io.bacta.login.server.service;

import io.bacta.game.message.ErrorMessage;
import io.bacta.login.message.LoginClientToken;
import io.bacta.login.message.LoginIncorrectClientId;
import io.bacta.login.message.ServerNowEpochTime;
import io.bacta.login.message.SetSessionKey;
import io.bacta.login.server.LoginServerProperties;
import io.bacta.login.server.session.Session;
import io.bacta.login.server.session.SessionException;
import io.bacta.login.server.session.SessionService;
import io.bacta.shared.crypto.KeyShare;
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
    private final KeyShare keyShare;
    private final SessionService sessionService;
    private final CharacterService characterService;
    private final GalaxyService galaxyService;
    private final String requiredClientVersion;

    @Inject
    public DefaultClientService(LoginServerProperties loginServerProperties,
                                SessionService sessionService,
                                CharacterService characterService,
                                KeyShare keyShare,
                                GalaxyService galaxyService,
                                @Value("${bacta.network.shared.requiredClientVersion}")
                                 String requiredClientVersion) {
        this.loginServerProperties = loginServerProperties;
        this.sessionService = sessionService;
        this.characterService = characterService;
        this.keyShare = keyShare;
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

        try {
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
        } catch (SessionException ex) {
            LOGGER.warn("Rejected client validation because session failed with message {}", ex.getMessage());

            final ErrorMessage message = new ErrorMessage("VALIDATION FAILED", "Your station Id was not valid. Wrong password? Account closed?");
            connection.sendMessage(message);
            connection.disconnect();
        }
    }


    @Override
    public void clientValidated(SoeConnection connection, int bactaId, String username, String sessionKey, boolean isSecure, int gameBits, int subscriptionBits) {
        //implement admin

        //Session key login: sessionId + accountId
        //Otherwise: accountId + isSecure + username

        //Create a key with:
        //If they logged in with a session, then the key will contain of the sessionId + accountId
        //Otherwise, the key is comprised of the accountId, if they are connecting with god client, and username
        //Encrypt the key before sending to client (not sure why, we've already exposed the sessionId as plain text)

        //The token
        byte[] data = new byte[10];

        final KeyShare.Token token = keyShare.cipherToken(data);
        sendLoginClientToken(connection, token, bactaId, username);

        galaxyService.sendClusterEnum(connection);
        galaxyService.sendDisabledCharacterCreationServers(connection);
        characterService.sendEnumerateCharacters(connection, bactaId);
        galaxyService.sendClusterStatus(connection);
    }

    private void sendLoginClientToken(SoeConnection connection, KeyShare.Token token, int bactaId, String username) {
        final LoginClientToken message = new LoginClientToken(token, bactaId, username);
        connection.sendMessage(message);
    }

    private void establishSessionMode(SoeConnection connection, String username, String password) throws SessionException {
        final Session session = sessionService.establish(username, password);

        //We need to send the "SetSessionKey" message to the client so that it knows about the session key.
        final SetSessionKey message = new SetSessionKey(session.getKey());
        connection.sendMessage(message);

        clientValidated(connection, session.getAccountId(), username, session.getKey(), false, 0, 0);
    }

    private void validateSessionMode(SoeConnection connection, String sessionKey) {
        //final Session session = sessionService.validate(sessionKey);
        //ErrorMessage err(
    }

    private void discoverSessionMode(SoeConnection connection, String id, String key) {
    }

    private boolean validateClientVersion(SoeConnection connection, String clientVersion) {
        //TODO: In the future, we might want to change up how versions are validated.
        if (loginServerProperties.isValidateClientVersionEnabled()
                && !requiredClientVersion.equals(clientVersion)) {

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
