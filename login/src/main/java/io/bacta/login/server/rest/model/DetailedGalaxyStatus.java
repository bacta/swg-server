package io.bacta.login.server.rest.model;

import lombok.Getter;

import java.util.Set;
import java.util.TreeSet;

@Getter
public final class DetailedGalaxyStatus {
    private String name;
    private String address;
    private int port;
    private int timeZone;

    private String branch;
    private String networkVersion;
    private int changeList;

    private int maxCharacters;
    private int maxCharactersPerAccount;
    private int onlinePlayerLimit;
    private int onlineTutorialLimit;
    private int onlineFreeTrialLimit;
    private boolean characterCreationDisabled;

    private boolean acceptingConnections;
    private boolean secret;
    private boolean locked;

    private final Set<ConnectionServerEntry> connectionServers = new TreeSet<>();

    @Getter
    public static final class ConnectionServerEntry {
        private int id;
        private String address;
        private int port;
        private int ping;
        private int totalConnected;
    }
}
