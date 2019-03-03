package io.bacta.login.server.service;

public class GalaxyNotFoundException extends Exception {
    private final String galaxyName;

    public GalaxyNotFoundException(String galaxyName) {
        super(String.format("Could not find galaxy named %s", galaxyName));
        this.galaxyName = galaxyName;
    }
}
