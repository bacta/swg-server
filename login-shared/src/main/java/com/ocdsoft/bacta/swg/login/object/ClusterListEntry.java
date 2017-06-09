package com.ocdsoft.bacta.swg.login.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Created by crush on 6/8/2017.
 */
@Getter
@Setter
@RequiredArgsConstructor
public final class ClusterListEntry {
    private final int clusterId;
    private final String clusterName;
    //List<ConnectionServerEntry> connectionServers;
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
    private int changelist;
    private String networkVersion;
}