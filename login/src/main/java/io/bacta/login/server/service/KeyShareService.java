package io.bacta.login.server.service;

import io.bacta.login.message.GalaxyEncryptionKey;
import io.bacta.shared.crypto.KeyShare;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Slf4j
@Service
public final class KeyShareService {
    private static final long UPDATE_INTERVAL = 1000 * 60; //60 seconds

    private final KeyShare keyShare;
    private final GalaxyService galaxyService;

    @Inject
    public KeyShareService(KeyShare keyShare,
                           GalaxyService galaxyService) {
        this.keyShare = keyShare;
        this.galaxyService = galaxyService;
    }

    @Scheduled(initialDelay = 0, fixedRate = UPDATE_INTERVAL)
    private void update() {
        LOGGER.debug("Updating login keys and sending to galaxies.");

        //Create a new key.
        final KeyShare.Key key = KeyShare.generateKey();
        keyShare.addKey(key);

        //Send it to all galaxies.
        final GalaxyEncryptionKey message = new GalaxyEncryptionKey(key);
        galaxyService.sendToAllGalaxies(message);
    }
}
