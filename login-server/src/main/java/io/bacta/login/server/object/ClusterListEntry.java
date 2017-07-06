/*
 * Copyright 2017. Bacta
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
 * INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package io.bacta.login.server.object;

import lombok.Getter;
import lombok.Setter;

import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Created by crush on 7/2/2017.
 */
@Getter
@Setter
public final class ClusterListEntry {
    private final int id;
    private final String name;
    //private CentralServerConnection galaxyServerConnection;
    private final SortedSet<ConnectionServerEntry> connectionServers;
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

    public ClusterListEntry(final int id, final String name) {
        this.id = id;
        this.name = name;
        this.connectionServers = new TreeSet<>();
    }
}
