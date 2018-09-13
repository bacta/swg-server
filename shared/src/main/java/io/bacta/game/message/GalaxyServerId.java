package io.bacta.game.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Login to galaxy server. Tells the galaxy server its cluster id in the login cluster.
 */
@Getter
@AllArgsConstructor
public final class GalaxyServerId {
    public final int galaxyId;
}
