package io.bacta.game.message;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Galaxy to login. Tells login that this galaxy has come online, and is ready to receive it's cluster identity.
 * Login should respond with GalaxyServerId containing the cluster id for the galaxy.
 */
@Getter
@AllArgsConstructor
public final class GalaxyServerOnline {
    private final String name;
    private final String address;
    private final short port;
}
