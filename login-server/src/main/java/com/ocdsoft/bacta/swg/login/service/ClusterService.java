package com.ocdsoft.bacta.swg.login.service;

import com.ocdsoft.bacta.soe.network.connection.SoeUdpConnection;
import com.ocdsoft.bacta.soe.network.message.GameNetworkMessage;
import com.ocdsoft.bacta.swg.login.GalaxyRegistrationFailedException;
import com.ocdsoft.bacta.swg.login.LoginServerProperties;
import com.ocdsoft.bacta.swg.login.object.ClusterListEntry;

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

    /**
     * Gets the latest information about each registered cluster from the repository.
     */
    void refreshClusterList();

    /**
     * Attempts to register a new galaxy cluster. This requires that {@link LoginServerProperties#autoGalaxyRegistrationEnabled}
     * be set to true.
     *
     * @param clusterName The name of the cluster attempting to register
     * @param address     The host name or IP address of the server.
     * @param port        The service port for the server.
     * @return An entry representing this new cluster.
     * @throws GalaxyRegistrationFailedException If auto galaxy registration is disabled, or another galaxy already exists
     *                                           at the same name or address.
     */
    ClusterListEntry registerCluster(String clusterName, String address, short port)
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
     * Sends status of all clusters to a specific client connection.
     */
    void sendClusterStatus(SoeUdpConnection connection);

    /**
     * Sends status of all clusters to all current client connections.
     */
    void sendClusterStatusToAll();

    /**
     * Sends extended status of all clusters to specific client connection.
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
//
//public final class ClusterService {
//    private final ClusterRepository clusterRepository;
//
//    private final List<ClusterListEntry> clusterList;
//    private final LoginServerProperties loginServerProperties;
//
//    public ClusterService(ClusterRepository clusterRepository,
//                          LoginServerProperties loginServerProperties) {
//        this.clusterRepository = clusterRepository;
//        this.loginServerProperties = loginServerProperties;
//
//        this.clusterList = new ArrayList<>();
//    }
//
//    public ClusterListEntry findClusterById(final int clusterId) {
//        return this.clusterList.stream()
//                .filter(cle -> cle.getId() == clusterId)
//                .findFirst()
//                .orElse(null);
//    }
//
//    public void updateClusterData() {
//        for (final ClusterData clusterData : clusterRepository.findAll()) {
//            ClusterListEntry cle = findClusterById(clusterData.getId());
//
//            if (cle == null) {
//                //A new cluster is being added.
//            } else {
//                //We are updating an existing cluster entry.
//            }
//        }
//    }
//
//    public void sendClusterStatus() {
//    }
//
//    public void sendExtendedClusterStatus() {
//    }
//
//    /**
//     * Registers a new cluster with the cluster service and adds it to the {@link ClusterRepository}. This method will
//     * fail if {@link LoginServerProperties#autoGalaxyRegistrationEnabled} is false.
//     *
//     * @param clusterName
//     * @param address
//     * @throws GalaxyRegistrationFailedException If autoGalaxyRegistration is disabled.
//     */
//    public ClusterListEntry registerCluster(final String clusterName,
//                                            final InetSocketAddress address)
//            throws GalaxyRegistrationFailedException {
//
//        if (!loginServerProperties.isAutoGalaxyRegistrationEnabled()) {
//            throw new GalaxyRegistrationFailedException(clusterName, address,
//                    String.format("Galaxy {} from {} could not be registered because automatic galaxy registration is disabled.",
//                            clusterName, address));
//        }
//    }
//}
