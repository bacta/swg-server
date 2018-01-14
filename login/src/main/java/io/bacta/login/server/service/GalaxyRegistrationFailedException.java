package io.bacta.login.server.service;

import lombok.Getter;

@Getter
public final class GalaxyRegistrationFailedException extends Exception {
    private final String galaxyName;
    private final String address;
    private final int port;

    public GalaxyRegistrationFailedException(String galaxyName, String address, int port, String message) {
        super(message);

        this.galaxyName = galaxyName;
        this.address = address;
        this.port = port;
    }
}
