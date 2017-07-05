package bacta.io.login.server.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created by crush on 7/2/2017.
 */
@Getter
@Setter
@RequiredArgsConstructor
public final class ClusterListEntry {
    private final int id;
    private final String name;
    //private CentralServerConnection galaxyServerConnection;
    private final List<ConnectionServerEntry> connectionServers;
    private int numPlayers;
    private int numFreeTrialPlayers;
    private int numTutorialPlayers;
    private int maxCharacters;
    private int maxCharactersPerAccount;
    private int onlinePlayerLimit;
    private int onlineFreeTrialLimit;
    private int onlineTutorialLimit;
    private boolean freeTrialCanCreateChar;
    private int timeZone;
    private boolean connected;
    private String address;
    private short port;
    private boolean allowReconnect;
    private boolean secret;
    private boolean readyForPlayers;
    private boolean locked;
    private boolean notRecommendedDatabase;
    private boolean notRecommendedCentral;
    private String branch;
    private int changeList;
    private String networkVersion;
}
