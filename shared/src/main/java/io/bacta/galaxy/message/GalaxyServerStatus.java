package io.bacta.galaxy.message;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This message is typically sent to the login server to announce the current status of the galaxy server and set
 * various information about the galaxy like characters and limits on player counts.
 */
@Getter
@RequiredArgsConstructor
public final class GalaxyServerStatus {
    /**
     * The unique galaxy id that the login server responded with in {@link io.bacta.galaxy.message.GalaxyServerId}.
     */
    private final int id;
    /**
     * The name of the galaxy. This may change, but it is not recommended to change it in this way.
     */
    private final String name;
    /**
     * The timezone in which the galaxy server is located. This value is used by the client to determine how close it
     * is to the client's timezone.
     */
    private final int timeZone;
    private final int maxCharacters;
    private final int maxCharactersPerAccount;
    private final int onlinePlayerLimit;
    private final int onlineTutorialLimit;
    private final int onlineFreeTrialLimit;
    private final boolean allowCharacterCreation;
    private final boolean allowFreeTrialCharacterCreation;
    /**
     * The galaxy is ready for clients to start connecting. If it is also in a locked state, then only privileged
     * clients can connect.
     */
    private final boolean acceptingConnections;
    /**
     * The galaxy will not be available to clients that are outside of the local network.
     */
    private final boolean secret;
    /**
     * The galaxy is accepting connections, but is in a locked state. Only privileged clients may connect.
     */
    private final boolean locked;
}
