package io.bacta.galaxy.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * This message is typically sent to the login server to announce the current status of the galaxy server and set
 * various information about the galaxy like characters and limits on player counts.
 */
@Getter
@Setter
@NoArgsConstructor
public final class GalaxyServerStatus {
    /**
     * The name of the galaxy. If the name is changing in this status request, then the original name must be
     * supplied anterior to this message.
     */
    private String name;
    /**
     * The address at which the galaxy server can be reached.
     */
    private String address;
    /**
     * The port at which the galaxy server can be reached.
     */
    private int port;
    /**
     * The timezone in which the galaxy server is located. This value is used by the client to determine how close it
     * is to the client's timezone.
     */
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
    private boolean freeTrialCharacterCreationDisabled;

    /**
     * The galaxy is ready for clients to start connecting. If it is also in a locked state, then only privileged
     * clients can connect.
     */
    private boolean acceptingConnections;
    /**
     * The galaxy will not be available to clients that are outside of the local network.
     */
    private boolean secret;
    /**
     * The galaxy is accepting connections, but is in a locked state. Only privileged clients may connect.
     */
    private boolean locked;

    private final Set<ConnectionServerEntry> connectionServers = new HashSet<>();

    public GalaxyServerStatus(String name, String address, int port, int timeZone) {
        this.name = name;
        this.address = address;
        this.port = port;
        this.timeZone = timeZone;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class ConnectionServerEntry {
        private int id;
        private String address;
        private int port;
        private int ping;
        private int totalConnected;
    }
}
