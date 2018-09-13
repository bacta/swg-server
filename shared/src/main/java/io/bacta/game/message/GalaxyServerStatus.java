package io.bacta.game.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public final class GalaxyServerStatus {
    private final int galaxyId;
    private final String name;
    private final String address;
    private final short port;
    private final short pingPort;
    private final int timeZone;
    private final int maxCharacters;
    private final int maxCharactersPerAccount;
    private final int onlinePlayerLimit;
    private final int onlineTutorialLimit;
    private final int onlineFreeTrialLimit;
    private final boolean characterCreationEnabled;
    private final boolean acceptingConnections;
    private final boolean secret;
    private final boolean locked;
}
