package io.bacta.login.server.service;

import io.bacta.game.message.ErrorMessage;
import io.bacta.login.message.LoginClientToken;
import io.bacta.login.message.LoginIncorrectClientId;
import io.bacta.login.message.ServerNowEpochTime;
import io.bacta.login.message.SetSessionKey;
import io.bacta.login.server.LoginServerProperties;
import io.bacta.login.server.session.SessionException;
import io.bacta.login.server.session.SessionToken;
import io.bacta.login.server.session.SessionTokenProvider;
import io.bacta.soe.network.connection.ConnectionRole;
import io.bacta.soe.network.connection.SoeConnection;
import io.bacta.soe.network.message.TerminateReason;
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
    private final SessionTokenProvider sessionTokenProvider;
    private final CharacterService characterService;
    private final GalaxyService galaxyService;
    private final String requiredClientVersion;

    @Inject
    public DefaultClientService(LoginServerProperties loginServerProperties,
                                SessionTokenProvider sessionTokenProvider,
                                CharacterService characterService,
                                GalaxyService galaxyService,
                                @Value("${io.bacta.network.requiredClientVersion}") String requiredClientVersion) {

        this.loginServerProperties = loginServerProperties;
        this.sessionTokenProvider = sessionTokenProvider;
        this.characterService = characterService;
        this.galaxyService = galaxyService;
        this.requiredClientVersion = requiredClientVersion;
    }

    @Override
    public void validateClient(SoeConnection connection, String clientVersion, String id, String key) {
        try {
            //Client wants to know the difference in time between the server and client.
            final int epoch = (int) (System.currentTimeMillis() / 1000);

            LOGGER.info("Sending server epoch {} to client {}.", epoch, connection.getSoeUdpConnection().getRemoteAddress());

            final ServerNowEpochTime serverEpoch = new ServerNowEpochTime(epoch);
            connection.sendMessage(serverEpoch);

            //Make sure the client version is correct. If not, then we will catch that and send them a message.
            ensureClientVersion(clientVersion);

            switch (loginServerProperties.getSessionMode()) {
                case ESTABLISH:
                    establishSessionMode(connection, id, key);
                    break;
                case VALIDATE:
                    validateSessionMode(connection, id, key);
                    break;
                case DISCOVER:
                default: {
                    discoverSessionMode(connection, id, key);
                    break;
                }
            }

            //If they got to this point, then they've been authenticated. Set their connection roles.
            connection.addRole(ConnectionRole.AUTHENTICATED);

        } catch (SessionException ex) {
            LOGGER.warn("Rejected client validation because session failed with message {}", ex.getMessage());

            final ErrorMessage message = new ErrorMessage("VALIDATION FAILED", "Your credentials were rejected by the server.");
            connection.sendMessage(message);
            connection.disconnect(TerminateReason.REFUSED, false);

        } catch (InvalidClientException ex) {
            LOGGER.warn("Client {} tried to establish with version {} but {} was required.",
                    connection.getSoeUdpConnection().getRemoteAddress(),
                    ex.getClientVersion(),
                    requiredClientVersion);

            final String serverApplicationVersion = ""; //SOE only sent this in debug mode. Not sure we care to send it at all.

            final LoginIncorrectClientId incorrectId = new LoginIncorrectClientId(requiredClientVersion, serverApplicationVersion);
            connection.sendMessage(incorrectId);
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
        //byte[] data = new byte[10];

//        final KeyShare.Token token = keyShare.cipherToken(data);
//        sendLoginClientToken(connection, token, bactaId, username);

        //Ignore the above since we are doing session with JWT now.
        //Using JWT, we don't have to cipher any tokens. We can pass it straight to the game server.

        sendLoginClientToken(connection, sessionKey, bactaId, username);

        galaxyService.sendClusterEnum(connection);
        galaxyService.sendDisabledCharacterCreationServers(connection);
        characterService.sendEnumerateCharacters(connection, bactaId);
        galaxyService.sendClusterStatus(connection);
    }

    private void sendLoginClientToken(SoeConnection connection, String token, int bactaId, String username) {
        final LoginClientToken message = new LoginClientToken(token, bactaId, username);
        connection.sendMessage(message);
    }

    private void establishSessionMode(SoeConnection connection, String username, String password)
            throws SessionException {
        final SessionToken sessionToken = sessionTokenProvider.Provide(username, password);

        //We need to send the "SetSessionKey" message to the client so that it knows about the session key.
        final SetSessionKey message = new SetSessionKey(sessionToken.getToken());
        connection.sendMessage(message);

        //TODO: game bits, subscription bits, is secure? Do we care?
        final boolean isSecure = false;
        final int gameBits = 0;
        final int subscriptionBits = 0;

        clientValidated(
                connection,
                sessionToken.getAccountId(),
                username,
                sessionToken.getToken(),
                isSecure,
                gameBits,
                subscriptionBits);
    }

    private void validateSessionMode(SoeConnection connection, String id, String sessionKey) {
        //Communicate with auth server and check if the session key validates, and is issued by the correct issuer, etc.
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    private void discoverSessionMode(SoeConnection connection, String id, String key) {
        //first try validate key, then try establish if that fails.
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    private void ensureClientVersion(final String clientVersion) throws InvalidClientException {
        if (loginServerProperties.isValidateClientVersionEnabled() &&
                !requiredClientVersion.equals(clientVersion)) {
            throw new InvalidClientException(clientVersion);
        }
    }
}
