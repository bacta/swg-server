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

package io.bacta.login.server.service;

import io.bacta.login.server.GalaxyRegistrationFailedException;
import io.bacta.login.server.object.ClusterListEntry;
import io.bacta.shared.GameNetworkMessage;
import io.bacta.soe.network.connection.SoeUdpConnection;

/**
 * Created by crush on 7/2/2017.
 */
public interface ClusterService {
    /**
     * Finds a cluster by its cluster id.
     *
     * @param clusterId The id of the cluster to find.
     * @return The entry for the cluster, or null if not found.
     */
    ClusterListEntry findClusterById(int clusterId);

    /**
     * Finds a cluster by its cluster name.
     *
     * @param clusterName The name of the cluster to find.
     * @return The entry for the cluster, or null if not found.
     */
    ClusterListEntry findClusterByName(String clusterName);

    ClusterListEntry findClusterByAddress(String address, short port);

    /**
     * Gets the latest information about each registered cluster from the repository.
     */
    void refreshClusterList();


    /**
     * Attempts to connect a galaxy that is announcing itself to the login server. If the galaxy isn't registered with
     * the LoginServer, then it will be rejected unless the LoginServer is configured to allow automatic galaxy
     * registration.
     *
     * @param connection     The connection from the incoming galaxy server.
     * @param clusterName    The name of the galaxy that is connecting. This must match what is in the repository for the
     *                       galaxy at the given address:port combination, or it will be rejected.
     * @param timeZone       The timezone in which the galaxy is located.
     * @param networkVersion The version of the network the galaxy is using. If this doesn't match that of the LoginServer
     *                       then the connection will be terminated.
     * @return The ClusterListEntry matching this galaxy.
     * @throws GalaxyRegistrationFailedException If the galaxy is unable to register with the LoginServer.
     */
    ClusterListEntry connectCluster(SoeUdpConnection connection, String clusterName, int timeZone, String networkVersion)
            throws GalaxyRegistrationFailedException;

    /**
     * Removes the connection to a specified cluster.
     *
     * @param clusterListEntry The cluster to disconnect.
     * @param forceDisconnect  Drops the network connection if true. Otherwise, assumes connection already dropped.
     * @param reconnect        Attempt to re-establish connection to the cluster.
     */
    void disconnectCluster(ClusterListEntry clusterListEntry, boolean forceDisconnect, boolean reconnect);

    /**
     * Sends the enumeration of all known clusters, even if they are offline, to the specific client connection.
     *
     * @param connection The connection which will receive the message.
     */
    void sendClusterEnum(SoeUdpConnection connection);

    /**
     * Sends a list of any galaxies that have character creation disabled.
     *
     * @param connection The connection which will receive the message.
     */
    void sendDisabledCharacterCreationServers(SoeUdpConnection connection);

    /**
     * Sends status of all active clusters to a specific client connection.
     *
     * @param connection The connection which will receive the message.
     */
    void sendClusterStatus(SoeUdpConnection connection);

    /**
     * Sends status of all active clusters to all current client connections.
     */
    void sendClusterStatusToAll();

    /**
     * Sends extended status of all active clusters to specific client connection.
     *
     * @param connection The connection which will receive the message.
     */
    void sendExtendedClusterStatus(SoeUdpConnection connection);

    /**
     * Sends the specified message to the specified cluster.
     *
     * @param clusterId The id of the cluster to which to send the message.
     * @param message   The message being sent.
     */
    void sendToCluster(int clusterId, GameNetworkMessage message);
}