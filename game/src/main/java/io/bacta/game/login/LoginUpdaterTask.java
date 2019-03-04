package io.bacta.game.login;

import io.bacta.galaxy.message.GalaxyServerStatus;
import io.bacta.game.GameServerProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.Set;

@Slf4j
@Service
public final class LoginUpdaterTask {
    private static final int UPDATE_INTERVAL = 5000;

    private final LoginRestClient restClient;
    private final GameServerProperties properties;

    @Inject
    public LoginUpdaterTask(LoginRestClient restClient,
                            GameServerProperties properties) {
        this.restClient = restClient;
        this.properties = properties;
    }

    @Scheduled(initialDelay = UPDATE_INTERVAL, fixedRate = UPDATE_INTERVAL)
    private void updateGalaxyStatus() throws UnsupportedEncodingException {
        LOGGER.info("Updating login with current status.");

        final int timeZone = ZonedDateTime.now().getOffset().getTotalSeconds();

        final GalaxyServerStatus status = new GalaxyServerStatus(
                properties.getGalaxyName(),
                properties.getBindAddress().getHostAddress(),
                properties.getBindPort(),
                timeZone);

        status.setAcceptingConnections(true);
        status.setMaxCharacters(10000);
        status.setMaxCharactersPerAccount(10);
        status.setOnlinePlayerLimit(3000);
        status.setOnlineTutorialLimit(100);
        status.setOnlineFreeTrialLimit(500);

        final GalaxyServerStatus.ConnectionServerEntry connectionServer = new GalaxyServerStatus.ConnectionServerEntry(
                1,
                properties.getBindAddress().getHostAddress(),
                properties.getBindPort(),
                properties.getBindPingPort(),
                0);

        final Set<GalaxyServerStatus.ConnectionServerEntry> connectionServers = status.getConnectionServers();
        connectionServers.add(connectionServer);

        this.restClient.updateStatus(properties.getGalaxyName(), status);
    }
}
