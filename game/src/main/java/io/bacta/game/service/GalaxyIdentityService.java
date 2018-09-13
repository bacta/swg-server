package io.bacta.game.service;

import io.bacta.soe.network.connection.SoeConnection;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * Manages the galaxy's identity, and provides means of communicating that to other servers.
 */
@Slf4j
@Service
public final class GalaxyIdentityService {
    private final String galaxyName;
    private final ZoneOffset timeZoneOffset = ZonedDateTime.now().getOffset();
    private final String networkVersion;

    @Inject
    public GalaxyIdentityService(@Value("${io.bacta.game.galaxyName}") String galaxyName){
        this.galaxyName = galaxyName;
        this.networkVersion = ""; //TODO: network version - do we even care?
    }

    public void sendGalaxyServerId(SoeConnection connection) {
        LOGGER.info("Sending GalaxyServerId message to login server.");

        //final GalaxyServerId msg = new GalaxyServerId(galaxyName, timeZoneOffset.getTotalSeconds(), networkVersion);
        //connection.sendMessage(msg);
    }
}