package io.bacta.game.login;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public final class LoginUpdaterTask {
    /*private static final int UPDATE_INTERVAL = 5000;

    private final LoginRestClient restClient;
    private final ConnectionServerConfiguration connectionProperties;
    private final GameServerProperties gameProperties;

    @Inject
    public LoginUpdaterTask(final LoginRestClient restClient,
                            final ConnectionServerConfiguration connectionProperties,
                            final GameServerProperties gameProperties) {
        this.restClient = restClient;
        this.connectionProperties = connectionProperties;
        this.gameProperties = gameProperties;
    }

    @Scheduled(initialDelay = UPDATE_INTERVAL, fixedRate = UPDATE_INTERVAL)
    private void updateGalaxyStatus() throws UnsupportedEncodingException {
        LOGGER.debug("Updating login with current status.");

        final int timeZone = ZonedDateTime.now().getOffset().getTotalSeconds();

        final GalaxyServerStatus status = new GalaxyServerStatus(
                gameProperties.getGalaxyName(),
                connectionProperties.getBindAddress().getHostAddress(),
                connectionProperties.getBindPort(),
                timeZone);

        status.setAcceptingConnections(true);
        status.setMaxCharacters(10000);
        status.setMaxCharactersPerAccount(10);
        status.setOnlinePlayerLimit(3000);
        status.setOnlineTutorialLimit(100);
        status.setOnlineFreeTrialLimit(500);

        final GalaxyServerStatus.ConnectionServerEntry connectionServer = new GalaxyServerStatus.ConnectionServerEntry(
                1,
                connectionProperties.getBindAddress().getHostAddress(),
                connectionProperties.getBindPort(),
                connectionProperties.getBindPingPort(),
                0);

        final Set<GalaxyServerStatus.ConnectionServerEntry> connectionServers = status.getConnectionServers();
        connectionServers.add(connectionServer);

        this.restClient.updateStatus(gameProperties.getGalaxyName(), status);
    }*/
}
