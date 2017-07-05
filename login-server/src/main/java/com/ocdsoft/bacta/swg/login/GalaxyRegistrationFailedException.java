package com.ocdsoft.bacta.swg.login;

import lombok.Getter;

import java.net.InetSocketAddress;

/**
 * Created by crush on 7/3/2017.
 */
@Getter
public final class GalaxyRegistrationFailedException extends Exception {
    private final String galaxyName;
    private final InetSocketAddress galaxyAddress;

    public GalaxyRegistrationFailedException(final String galaxyName,
                                             final InetSocketAddress galaxyAddress,
                                             final String message) {
        super(message);

        this.galaxyName = galaxyName;
        this.galaxyAddress = galaxyAddress;
    }
}
