package io.bacta.login.server.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@RequiredArgsConstructor
public final class GalaxyStatusUpdate {
    private final String name;
    private final String address;
    private final int port;
    private final int timeZone;

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

    private final Set<ConnectionServerEntry> connectionServers;
}
